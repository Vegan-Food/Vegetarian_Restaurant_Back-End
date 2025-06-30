package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.dto.request.AuthRequest;
import com.veganfood.veganfoodbackend.dto.response.AuthResponse;
import com.veganfood.veganfoodbackend.dto.GoogleLoginDTO;
import com.veganfood.veganfoodbackend.model.User;
import com.veganfood.veganfoodbackend.repository.UserRepository;
import com.veganfood.veganfoodbackend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔐 Đăng nhập bằng email/password
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null || user.getRole() == User.Role.customer) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Không thể đăng nhập. Hãy dùng Google hoặc kiểm tra email.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Sai mật khẩu");
        }

        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole().name()));
    }

    // Đăng nhập Google
    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginDTO googleData) {
        String email = googleData.getEmail();
        String name = googleData.getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setRole(User.Role.customer);
            user.setPhoneNumber("");
            user.setAddress("");
            user = userRepository.save(user);
        }

        if (user.getRole() != User.Role.customer) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Không phải khách hàng.");
        }

        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole().name()));
    }

    // 🧪 API test mật khẩu mã hoá cứng
    @PostMapping("/debug-password")
    public String debugPassword() {
        String rawPassword = "123";
        String encodedPassword = "$2a$10$UIhYVPIuXgLU.rU6l64uEO6mL5iUQ9mL4PmyE9xRFaHRPXwFB6aCm";
        boolean match = passwordEncoder.matches(rawPassword, encodedPassword);
        return match ? "Khớp" : "Không khớp";
    }

    // ✅ API để encode mật khẩu tuỳ ý
    @PostMapping("/encode")
    public String encodePassword(@RequestBody Map<String, String> payload) {
        return passwordEncoder.encode(payload.get("rawPassword"));
    }
}
