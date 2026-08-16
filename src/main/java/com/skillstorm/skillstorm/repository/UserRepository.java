package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.skillstorm.skillstorm.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUsername (String username);
}
