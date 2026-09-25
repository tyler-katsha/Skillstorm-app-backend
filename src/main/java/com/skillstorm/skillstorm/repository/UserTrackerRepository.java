package com.skillstorm.skillstorm.repository;

import com.skillstorm.skillstorm.model.UserTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTrackerRepository extends JpaRepository<UserTracker,Integer> {
    @Query("SELECT ut FROM UserTracker ut WHERE ut.user.userId = :userId")
    Optional<UserTracker> findByUserId(@Param("userId") int userId);
}
