package com.veganfood.veganfoodbackend.repository;

import com.veganfood.veganfoodbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
