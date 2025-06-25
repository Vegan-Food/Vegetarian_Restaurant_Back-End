package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.dto.CreateUserRequest;
import com.veganfood.veganfoodbackend.dto.UserDTO;
import com.veganfood.veganfoodbackend.model.User;
import com.veganfood.veganfoodbackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserRequest req) {
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        user.setRole(req.getRole());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setGoogleId(req.getGoogleId());
        user.setAddress(req.getAddress());
        return userService.createUser(user);
    }
}
