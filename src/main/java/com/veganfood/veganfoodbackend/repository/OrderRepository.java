package com.veganfood.veganfoodbackend.repository;

import com.veganfood.veganfoodbackend.model.Order;
import com.veganfood.veganfoodbackend.model.OrderItem;
import com.veganfood.veganfoodbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user); // Xem đơn hàng của 1 user
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user.userId = :userId")
    BigDecimal getTotalAmountByUserId(@Param("userId") Integer userId);
}
