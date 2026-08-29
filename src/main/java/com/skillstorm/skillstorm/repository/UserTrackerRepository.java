package com.skillstorm.skillstorm.repository;

import com.skillstorm.skillstorm.model.UserTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTrackerRepository extends JpaRepository<UserTracker,Integer> {
    Optional<UserTracker> findByUserId(int userId);
}
