package com.veganfood.veganfoodbackend.service;

import com.veganfood.veganfoodbackend.dto.UserDTO;
import com.veganfood.veganfoodbackend.model.User;
import com.veganfood.veganfoodbackend.repository.UserRepository;
import com.veganfood.veganfoodbackend.repository.OrderRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       OrderService orderService, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
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

    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User createStaffManager(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() != User.Role.staff && user.getRole() != User.Role.manager) {
            throw new IllegalArgumentException("Chỉ có thể tạo tài khoản với role staff hoặc manager");
        }
        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    // ✅ CHỈNH SỬA: Gán thêm totalAmount cho từng UserDTO
    public List<UserDTO> getUsersByRole(User.Role role) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == role)
                .map(user -> {
                    UserDTO dto = new UserDTO(user);
                    BigDecimal total = orderRepository.getTotalAmountByUserId(user.getUserId());
                    dto.setTotalAmount(total != null ? total : BigDecimal.ZERO);
                    return dto;
                })
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

    public BigDecimal getTotalSpentByEmail(String email) {
        return orderService.getTotalSpentByUser(email);
    }
}
