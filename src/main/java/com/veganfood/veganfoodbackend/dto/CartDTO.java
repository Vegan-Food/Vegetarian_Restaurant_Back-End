package com.veganfood.veganfoodbackend.dto;

import com.veganfood.veganfoodbackend.model.Cart;
import com.veganfood.veganfoodbackend.model.User;

public class CartDTO {
    private Integer cartId;
    private Integer userId;

    public CartDTO() {
    }

    public CartDTO(Cart cart) {
        this.cartId = cart.getCartId();
        User user = cart.getUser();
        this.userId = user != null ? user.getUserId() : null;
    }


    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
