package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.skillstorm.skillstorm.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername (String username);

    List<User> findTop10ByOrderByXpDesc();
}
