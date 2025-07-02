package com.veganfood.veganfoodbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateFeedbackDTO {
    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotBlank(message = "Comment is required")
    private String comment;

    // Constructors
    public CreateFeedbackDTO() {}

    public CreateFeedbackDTO(Integer userId, Integer productId, String comment) {
        this.userId = userId;
        this.productId = productId;
        this.comment = comment;
    }

    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    @Override
    public String toString() {
        return "CreateFeedbackDTO{" +
                "userId=" + userId +
                ", productId=" + productId +
                ", comment='" + comment + '\'' +
                '}';
    }
}