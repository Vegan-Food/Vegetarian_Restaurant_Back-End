package com.veganfood.veganfoodbackend.service;

import com.veganfood.veganfoodbackend.dto.ProductDTO;
import com.veganfood.veganfoodbackend.model.Product;
import com.veganfood.veganfoodbackend.repository.OrderItemRepository;
import com.veganfood.veganfoodbackend.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // ✅ Lấy tất cả sản phẩm
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    // ✅ Lấy sản phẩm theo category (không phân biệt hoa thường)
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category).stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    // ✅ Lấy 1 sản phẩm theo ID
    public ProductDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return new ProductDTO(product);
    }

    // ✅ Tạo sản phẩm mới
    public Product createProduct(Product product) {
        product.setTotal_order(0); // mặc định khi mới tạo
        return productRepository.save(product);
    }

    // ✅ Cập nhật sản phẩm
    public Product updateProduct(Integer id, Product updated) {
        return productRepository.findById(id).map(p -> {
            p.setName(updated.getName());
            p.setDescription(updated.getDescription());
            p.setPrice(updated.getPrice());
            p.setStock_quantity(updated.getStock_quantity());
            p.setImage_url(updated.getImage_url());
            p.setCategory(updated.getCategory());
            return productRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // ✅ Xoá sản phẩm
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    // ✅ Đề xuất các sản phẩm đã order gần đây (4 món)
    public List<Product> getRecentRecommendedProducts(Integer userId) {
        Pageable limit = PageRequest.of(0, 4); // Lấy 4 món gần đây nhất
        return orderItemRepository.findRecentProductsByUserId(userId, limit);
    }

    // ✅ Public API để gọi từ controller
    public List<ProductDTO> getSuggestedProducts(Integer userId) {
        List<Product> products = getRecentRecommendedProducts(userId);
        return products.stream().map(ProductDTO::new).collect(Collectors.toList());
    }

}
