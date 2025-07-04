package com.veganfood.veganfoodbackend.service;

import com.veganfood.veganfoodbackend.dto.CartItemDTO;
import com.veganfood.veganfoodbackend.model.*;
import com.veganfood.veganfoodbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<CartItem> getCartItemsByUserId(Integer userId) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cartItemRepository.findByCart_CartId(cart.getCartId());
    }

    // 🔥 Thêm method trả về DTO
    public List<CartItemDTO> getCartItemsDTOByUserId(Integer userId) {
        List<CartItem> cartItems = getCartItemsByUserId(userId);
        return cartItems.stream()
                .map(CartItemDTO::new)
                .collect(Collectors.toList());
    }

    public CartItem addProductToCart(Integer userId, Integer productId, int quantity) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElse(new CartItem(cart, product, 0));
        cartItem.setQuantity(cartItem.getQuantity() + quantity);

        return cartItemRepository.save(cartItem);
    }

    public void removeProductFromCart(Integer userId, Integer productId) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));
        cartItemRepository.delete(cartItem);
    }

    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cartItemRepository.deleteByCart(cart);
    }
}