package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.dto.request.AuthRequest;
import com.veganfood.veganfoodbackend.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // Demo: kiểm tra email và password (bạn nên thay bằng kiểm tra thực tế)
        if ("user@example.com".equals(request.getEmail()) && "123".equals(request.getPassword())) {
            // Giả lập thông tin user
            User user = new User();
            user.setUserId(2);
            user.setName("John Doe");
            user.setEmail(request.getEmail());
            user.setPhoneNumber("123456789");

            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}
