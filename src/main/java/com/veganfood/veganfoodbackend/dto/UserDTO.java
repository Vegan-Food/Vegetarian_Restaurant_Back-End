package com.veganfood.veganfoodbackend.dto;

import com.veganfood.veganfoodbackend.model.User;
import java.time.LocalDateTime;

public class UserDTO {
    private Integer userId;
    private String name;
    private String email;
    private User.Role role;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private String googleId;
    private String googleToken; // ✅ Thêm dòng này
    private String address;

    public UserDTO() {
        // Bắt buộc để Jackson khởi tạo
    }

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.phoneNumber = user.getPhoneNumber();
        this.createdAt = user.getCreatedAt();
        this.googleId = user.getGoogleId();
        this.googleToken = user.getGoogleToken(); // ✅ Lấy từ entity
        this.address = user.getAddress();
    }

    // Getters
    public Integer getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public User.Role getRole() { return role; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getGoogleId() { return googleId; }
    public String getGoogleToken() { return googleToken; } // ✅ Getter mới
    public String getAddress() { return address; }
}
