package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.dto.CustomerDTO;
import com.veganfood.veganfoodbackend.dto.UserDTO;
import com.veganfood.veganfoodbackend.dto.UserPublicDTO;
import com.veganfood.veganfoodbackend.model.User;
import com.veganfood.veganfoodbackend.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ✅ Trả toàn bộ user (chỉ dùng cho quản trị)
    @GetMapping
    public List<UserDTO> getAllUsers() {
        System.out.println("Fetching all users...");
        return userService.getAllUsers();
    }

    // ✅ Trả thông tin công khai (không cần token)
    @GetMapping("/profile")
    public List<UserPublicDTO> getPublicUsers() {
        List<User> users = userService.getAllUsersRaw();
        return users.stream()
                .map(user -> new UserPublicDTO(
                        user.getName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getAddress()
                ))
                .toList();
    }
    @GetMapping("/customerprofile")
    public CustomerDTO getCustomerProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        return new CustomerDTO(
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getAddress()
        );
    }

    // ✅ Tạo user mới (public API)
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
