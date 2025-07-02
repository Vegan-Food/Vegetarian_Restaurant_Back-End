package com.veganfood.veganfoodbackend.dto;

public class CustomerDTO {
    private String email;
    private String name;
    private String phoneNumber;
    private String address;

    public CustomerDTO(String email, String name, String phoneNumber, String address) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }
}
