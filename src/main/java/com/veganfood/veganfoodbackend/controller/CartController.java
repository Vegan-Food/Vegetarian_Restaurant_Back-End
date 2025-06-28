package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.dto.CartResponseDTO;
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

    @PostMapping("/{userId}")
    public Cart createOrGetCart(@PathVariable Integer userId) {
        return cartService.getOrCreateCartByUserId(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCartWithItems(@PathVariable Integer userId) {
        CartResponseDTO response = cartService.getCartWithItems(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteCart(@PathVariable Integer userId) {
        cartService.deleteCartByUserId(userId);
        return ResponseEntity.ok("Cart deleted successfully");
    }
}
