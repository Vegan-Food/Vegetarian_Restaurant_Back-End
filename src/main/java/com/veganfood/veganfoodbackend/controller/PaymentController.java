package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.model.Order;
import com.veganfood.veganfoodbackend.repository.OrderRepository;
import com.veganfood.veganfoodbackend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/create/{orderId}")
    public ResponseEntity<?> createPayment(@PathVariable Integer orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
        }

        Order order = orderOptional.get();

        // ✅ Lấy user hiện tại từ context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // lấy email từ token

        // ✅ Kiểm tra quyền sở hữu đơn hàng
        if (!order.getUser().getEmail().equals(email)) {
            return ResponseEntity.status(403).body("Bạn không có quyền truy cập đơn hàng này");
        }

        if (!order.getStatus().equals(Order.OrderStatus.pending)) {
            return ResponseEntity.badRequest().body("Đơn hàng không ở trạng thái chờ thanh toán");
        }

        String paymentLink = paymentService.createPaymentLink(order.getOrderId(), order.getTotalAmount(), order.getUser().getName());
        return ResponseEntity.ok(Map.of("checkoutUrl", paymentLink));
    }

}
