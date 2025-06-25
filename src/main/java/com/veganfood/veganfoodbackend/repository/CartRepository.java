package com.veganfood.veganfoodbackend.repository;

import com.veganfood.veganfoodbackend.model.Cart;
import com.veganfood.veganfoodbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserUserId(Integer userId); // Thêm dòng này để dùng userId trực tiếp
}
