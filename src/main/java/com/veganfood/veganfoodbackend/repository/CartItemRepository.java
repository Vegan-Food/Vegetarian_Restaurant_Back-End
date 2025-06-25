package com.veganfood.veganfoodbackend.repository;

import com.veganfood.veganfoodbackend.model.Cart;
import com.veganfood.veganfoodbackend.model.CartItem;
import com.veganfood.veganfoodbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product); // Thêm dòng này
    List<CartItem> findByCart(Cart cart); // Có thể dùng nếu cần lấy tất cả sản phẩm trong cart
    void deleteByCart(Cart cart); // Nếu muốn xóa toàn bộ item trong cart
}
