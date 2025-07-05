package com.veganfood.veganfoodbackend.controller;

import com.veganfood.veganfoodbackend.dto.UpdateProfileDTO;
import com.veganfood.veganfoodbackend.dto.UserDTO;
import com.veganfood.veganfoodbackend.dto.UserPublicDTO;
import com.veganfood.veganfoodbackend.dto.ViewProfileDTO;
import com.veganfood.veganfoodbackend.dto.request.CreateStaffManagerRequest;
import com.veganfood.veganfoodbackend.model.User;
import com.veganfood.veganfoodbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @GetMapping("/viewprofile")
    public ViewProfileDTO getCustomerProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        return new ViewProfileDTO(
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getDateOfBirth(),
                user.getGender(),
                user.getCreatedAt()
        );
    }

    @PostMapping("/viewprofile/update")
    public ViewProfileDTO updateProfile(@RequestBody UpdateProfileDTO dto, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        user.setName(dto.getName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setGender(dto.getGender());

        userService.save(user); // Cần có hàm save trong service

        return new ViewProfileDTO(
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getDateOfBirth(),
                user.getGender(),
                user.getCreatedAt()
        );
    }


    // ✅ Tạo user mới (public API)
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // 🆕 API tạo tài khoản staff/manager (chỉ owner)
    @PostMapping("/create-staff-manager")
    @PreAuthorize("hasRole('ROLE_owner')")
    public ResponseEntity<?> createStaffManager(@RequestBody CreateStaffManagerRequest request, Authentication authentication) {
        try {
            // Kiểm tra role hợp lệ
            if (request.getRole() != User.Role.staff && request.getRole() != User.Role.manager) {
                return ResponseEntity.badRequest()
                        .body("Chỉ có thể tạo tài khoản với role staff hoặc manager");
            }

            // Kiểm tra email đã tồn tại
            if (userService.isEmailExists(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body("Email đã tồn tại trong hệ thống");
            }

            // Tạo user mới
            User newUser = new User();
            newUser.setName(request.getName());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(request.getPassword()); // Sẽ được mã hóa trong service
            newUser.setRole(request.getRole());
            newUser.setPhoneNumber(request.getPhoneNumber());
            newUser.setAddress(request.getAddress());

            User createdUser = userService.createStaffManager(newUser);

            // Trả về thông tin user đã tạo (không bao gồm password)
            UserDTO userDTO = new UserDTO(createdUser);

            return ResponseEntity.ok(Map.of(
                    "message", "Account created successfully",
                    "user", userDTO,
                    "createdBy", authentication.getName()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi tạo tài khoản: " + e.getMessage());
        }
    }
}