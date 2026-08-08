package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.skillstorm.skillstorm.model.Leaderboard;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, Integer> {}
