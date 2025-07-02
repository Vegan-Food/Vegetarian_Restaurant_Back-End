package com.veganfood.veganfoodbackend.service;

import com.veganfood.veganfoodbackend.dto.ProductDTO;
import com.veganfood.veganfoodbackend.model.Product;
import com.veganfood.veganfoodbackend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
}
