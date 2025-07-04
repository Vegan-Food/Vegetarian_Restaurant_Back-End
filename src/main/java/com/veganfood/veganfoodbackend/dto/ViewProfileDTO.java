package com.veganfood.veganfoodbackend.dto;

import java.time.LocalDate;

public class ViewProfileDTO {
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private LocalDate birthDate; // ✅ Ngày sinh
    private String gender;       // ✅ Giới tính

    public ViewProfileDTO(String email, String name, String phoneNumber, String address, LocalDate birthDate, String gender) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.birthDate = birthDate;
        this.gender = gender;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }
}
