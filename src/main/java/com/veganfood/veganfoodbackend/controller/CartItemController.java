package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.model.CartItem;
import com.veganfood.veganfoodbackend.service.CartItemService;
import com.veganfood.veganfoodbackend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart/items")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private CartService cartService;

    @PostMapping("/{userId}/add")
    public CartItem addItemToCart(
            @PathVariable Integer userId,
            @RequestParam Integer productId,
            @RequestParam Integer quantity
    ) {
        cartService.getOrCreateCartByUserId(userId); // đảm bảo cart tồn tại
        return cartItemService.addProductToCart(userId, productId, quantity);
    }

    @GetMapping("/{userId}")
    public List<CartItem> getCartItems(@PathVariable Integer userId) {
        return cartItemService.getCartItemsByUserId(userId);
    }

    @DeleteMapping("/{userId}/remove")
    public void removeItemFromCart(
            @PathVariable Integer userId,
            @RequestParam Integer productId
    ) {
        cartItemService.removeProductFromCart(userId, productId);
    }

    @DeleteMapping("/{userId}/clear")
    public void clearCart(@PathVariable Integer userId) {
        cartItemService.clearCart(userId);
    }
}
