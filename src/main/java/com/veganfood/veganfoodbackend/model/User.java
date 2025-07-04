    package com.veganfood.veganfoodbackend.model;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "user")
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_id")
        private Integer userId;

        private String name;
        private String email;

        @JsonIgnore
        private String password;

        @Enumerated(EnumType.STRING)
        private Role role;

        private String phoneNumber;

        private LocalDateTime createdAt;

        private String googleId;

        @Column(name = "google_token")
        private String googleToken;  // ✅ Thêm thuộc tính này

        private String address;

        @PrePersist
        protected void onCreate() {
            this.createdAt = LocalDateTime.now();
        }

        public enum Role {
            owner, staff, customer, manager
        }

        // Getters and Setters
        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public String getGoogleId() {
            return googleId;
        }

        public void setGoogleId(String googleId) {
            this.googleId = googleId;
        }

        public String getGoogleToken() {
            return googleToken;
        }

        public void setGoogleToken(String googleToken) {
            this.googleToken = googleToken;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
