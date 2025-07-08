package com.veganfood.veganfoodbackend.repository;

import com.veganfood.veganfoodbackend.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Integer>{
    Optional<Discount> findByDiscountCode(String discountCode);
    List<Discount> findByStatus(Discount.Status status);
}
