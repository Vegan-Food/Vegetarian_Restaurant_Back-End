package com.veganfood.veganfoodbackend.repository;

import com.veganfood.veganfoodbackend.model.OrderItem;
import com.veganfood.veganfoodbackend.dto.TopOrderedProductDTO;
import com.veganfood.veganfoodbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderOrderId(Integer orderId);

    // ✅ Thêm truy vấn tìm sản phẩm được đặt nhiều nhất
    @Query(value = """
    SELECT 
        p.product_id AS productId,
        p.name AS name,
        p.image_url AS imageUrl,
        p.category AS category,
        SUM(oi.quantity) AS totalOrdered
    FROM order_item oi
    JOIN product p ON oi.product_id = p.product_id
    GROUP BY p.product_id, p.name, p.image_url, p.category
    ORDER BY totalOrdered DESC
    LIMIT 5
    """, nativeQuery = true)
    List<TopOrderedProductDTO> findTopOrderedProducts();

    @Query("""
    SELECT oi.product 
    FROM OrderItem oi 
    WHERE oi.order.user.id = :userId 
    ORDER BY oi.order.createdAt DESC
""")
    List<Product> findRecentProductsByUserId(@Param("userId") Integer userId, Pageable pageable);



}
