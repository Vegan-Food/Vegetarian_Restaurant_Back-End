package com.veganfood.veganfoodbackend.dto;

public interface TopOrderedProductDTO {
    Integer getProductId();
    String getName();
    String getImageUrl();
    String getCategory();
    Long getTotalOrdered(); // ← để khớp với SUM trong SQL
}
