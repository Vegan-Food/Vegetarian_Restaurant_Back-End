package com.veganfood.veganfoodbackend.dto;

public class CustomerDTO {
    private String name;
    private String phoneNumber;
    private String address;

    public CustomerDTO(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
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
