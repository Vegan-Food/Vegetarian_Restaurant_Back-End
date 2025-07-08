package com.veganfood.veganfoodbackend.service;

import com.veganfood.veganfoodbackend.model.Discount;
import com.veganfood.veganfoodbackend.repository.DiscountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public Discount create(Discount discount) {
        discount.setStatus(Discount.Status.Available);
        discount.setCreatedAt(java.time.LocalDateTime.now());
        return discountRepository.save(discount);
    }

    public List<Discount> getAll() {
        return discountRepository.findAll();
    }

    public Optional<Discount> findByCode(String code) {
        return discountRepository.findByDiscountCode(code);
    }

    public Discount useDiscount(String code) {
        Discount discount = discountRepository.findByDiscountCode(code)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        if (discount.getStatus() != Discount.Status.Available || discount.getQuantity() <= 0) {
            throw new RuntimeException("Mã giảm giá không còn khả dụng");
        }

        discount.setQuantity(discount.getQuantity() - 1);
        if (discount.getQuantity() == 0) {
            discount.setStatus(Discount.Status.Unavailable);
        }

        return discountRepository.save(discount);
    }

    public Discount updateStatus(Integer id, Discount.Status status) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã"));

        discount.setStatus(status);
        return discountRepository.save(discount);
    }
}
