package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.dto.OrderDTO;
import com.veganfood.veganfoodbackend.dto.request.CheckoutRequest;
import com.veganfood.veganfoodbackend.dto.request.UpdateOrderStatusRequest;
import com.veganfood.veganfoodbackend.model.User;
import com.veganfood.veganfoodbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ✅ API thanh toán
    @PostMapping("/checkout")
    public String checkout(@RequestBody CheckoutRequest request, Authentication authentication) {
        String email = authentication.getName(); // Lấy email từ JWT
        System.out.println("🔍 Checkout - Authenticated email: " + email);
        return orderService.checkoutByEmail(request, email);
    }

    // ✅ API cho user: Lấy đơn hàng của chính họ
    @GetMapping("/list")
    public List<OrderDTO> getOrdersList(Authentication authentication) {
        String email = authentication.getName(); // Lấy email từ JWT
        return orderService.getOrdersForUserOrAdmin(email);
    }

    // ✅ API cho admin/staff: Lấy tất cả đơn hàng
    @GetMapping("/")
    public List<OrderDTO> getAllOrders(Authentication authentication) {
        String email = authentication.getName(); // Lấy email từ JWT
        System.out.println("🔍 GetAllOrders - Authenticated email: " + email);
        return orderService.getOrdersForUserOrAdmin(email);
    }

    @DeleteMapping("/{orderId}")
    public String deleteOrder(@PathVariable Integer orderId, Authentication authentication) {
        String email = authentication.getName();
        return orderService.deleteOrderById(orderId, email);
    }

    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request.getStatus(), email));
    }




}
