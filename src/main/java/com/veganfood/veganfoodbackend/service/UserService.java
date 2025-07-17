package com.veganfood.veganfoodbackend.service;

import com.veganfood.veganfoodbackend.dto.UserDTO;
import com.veganfood.veganfoodbackend.model.User;
import com.veganfood.veganfoodbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsersRaw() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    // 🆕 Kiểm tra email đã tồn tại
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // 🆕 Tạo tài khoản staff/manager
    public User createStaffManager(User user) {
        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Đảm bảo chỉ tạo được staff hoặc manager
        if (user.getRole() != User.Role.staff && user.getRole() != User.Role.manager) {
            throw new IllegalArgumentException("Chỉ có thể tạo tài khoản với role staff hoặc manager");
        }

        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<UserDTO> getUsersByRole(User.Role role) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == role)
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByRoles(User.Role... roles) {
        List<User.Role> roleList = List.of(roles);
        return userRepository.findAll()
                .stream()
                .filter(user -> roleList.contains(user.getRole()))
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteUserById(Integer userId, String requestedByEmail) {
        User requestedBy = userRepository.findByEmail(requestedByEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người yêu cầu"));

        if (requestedBy.getRole() != User.Role.owner) {
            throw new RuntimeException("Chỉ owner mới có quyền xóa người dùng");
        }

        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng cần xóa"));

        if (userToDelete.getUserId().equals(requestedBy.getUserId())) {
            throw new RuntimeException("Không thể tự xóa chính mình");
        }

        if (userToDelete.getRole() == User.Role.owner) {
            throw new RuntimeException("Không thể xóa người dùng có vai trò owner");
        }

        userRepository.delete(userToDelete);
    }

    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        userRepository.delete(user);
    }



}