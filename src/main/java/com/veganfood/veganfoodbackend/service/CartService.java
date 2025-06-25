package com.veganfood.veganfoodbackend.service;

import com.veganfood.veganfoodbackend.model.Cart;
import com.veganfood.veganfoodbackend.model.User;
import com.veganfood.veganfoodbackend.repository.CartRepository;
import com.veganfood.veganfoodbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    public Cart getOrCreateCartByUserId(Integer userId) {
        // Lấy user từ userId
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Tìm cart theo user
        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        if (cartOpt.isPresent()) {
            return cartOpt.get();
        }

        // Nếu chưa có cart, tạo mới
        Cart newCart = new Cart();
        newCart.setUser(user);
        return cartRepository.save(newCart);
    }

    public void deleteCartByUserId(Integer userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserUserId(userId);
        if (cartOpt.isPresent()) {
            cartRepository.delete(cartOpt.get());
        } else {
            throw new RuntimeException("Cart not found for userId: " + userId);
        }
    }
}
