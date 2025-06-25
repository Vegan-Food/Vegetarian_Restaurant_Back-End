package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.model.Cart;
import com.veganfood.veganfoodbackend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // POST /api/cart/{userId} => Tạo mới hoặc lấy giỏ hàng theo user
    @PostMapping("/{userId}")
    public Cart createOrGetCart(@PathVariable Integer userId) {
        return cartService.getOrCreateCartByUserId(userId);
    }

    // GET /api/cart/{userId} => Dùng để test nhanh bằng trình duyệt
    @GetMapping("/{userId}")
    public Cart getCartByUserId(@PathVariable Integer userId) {
        return cartService.getOrCreateCartByUserId(userId);
    }

    // DELETE /api/cart/{userId} => Xóa giỏ hàng theo userId
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteCart(@PathVariable Integer userId) {
        cartService.deleteCartByUserId(userId);
        return ResponseEntity.ok("Cart deleted successfully");
    }
}
