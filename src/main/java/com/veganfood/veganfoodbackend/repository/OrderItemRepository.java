package com.veganfood.veganfoodbackend.repository;

import com.veganfood.veganfoodbackend.model.OrderItem;
import com.veganfood.veganfoodbackend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByOrderOrderId(Integer orderId);

}
