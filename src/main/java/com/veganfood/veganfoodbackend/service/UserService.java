package com.veganfood.veganfoodbackend.service;

import com.veganfood.veganfoodbackend.dto.UserDTO;
import com.veganfood.veganfoodbackend.model.User;
import com.veganfood.veganfoodbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
