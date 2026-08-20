package com.skillstorm.skillstorm.repository;

import com.skillstorm.skillstorm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.skillstorm.skillstorm.model.Leaderboard;

import java.util.Optional;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, Integer> {

    Optional<Leaderboard> findByUserUserId(int userId);
}
