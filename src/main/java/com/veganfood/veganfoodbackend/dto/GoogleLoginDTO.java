package com.veganfood.veganfoodbackend.dto;

public class GoogleLoginDTO {
    private String email;
    private String name;

    public GoogleLoginDTO() {
    }

    public GoogleLoginDTO(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
