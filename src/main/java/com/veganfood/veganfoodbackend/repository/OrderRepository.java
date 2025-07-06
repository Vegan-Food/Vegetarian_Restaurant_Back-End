package com.veganfood.veganfoodbackend.repository;

import com.veganfood.veganfoodbackend.model.Order;
import com.veganfood.veganfoodbackend.model.OrderItem;
import com.veganfood.veganfoodbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user); // Xem đơn hàng của 1 user

}
