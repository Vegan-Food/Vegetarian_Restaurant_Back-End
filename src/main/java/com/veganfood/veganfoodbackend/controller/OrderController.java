package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.dto.OrderDTO;
import com.veganfood.veganfoodbackend.dto.ProductDTO;
import com.veganfood.veganfoodbackend.dto.TopOrderedProductDTO;
import com.veganfood.veganfoodbackend.dto.request.CheckoutRequest;
import com.veganfood.veganfoodbackend.dto.request.UpdateOrderStatusRequest;
import com.veganfood.veganfoodbackend.model.Product;
import com.veganfood.veganfoodbackend.model.User;
import com.veganfood.veganfoodbackend.service.OrderService;
import com.veganfood.veganfoodbackend.service.ProductService;
import com.veganfood.veganfoodbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;


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

    // ✅ API lấy chi tiết đơn hàng theo orderId
    @GetMapping("/list/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer orderId, Authentication authentication) {
        String email = authentication.getName();
        OrderDTO order = orderService.getOrderByIdAndEmail(orderId, email);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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

    @GetMapping("/top-products")
    public ResponseEntity<List<TopOrderedProductDTO>> getTopOrderedProducts() {
        return ResponseEntity.ok(orderService.getTopOrderedProducts());
    }

    // ✅ API: Đề xuất sản phẩm gần đây
    @GetMapping("/suggested-products")
    @PreAuthorize("isAuthenticated()")
    public List<ProductDTO> getSuggestedProducts(Authentication authentication) {
        String email = authentication.getName(); // Lấy email từ token
        User user = userService.getUserByEmail(email); // Lấy user từ DB

        return productService.getSuggestedProducts(user.getUserId());
    }

}
