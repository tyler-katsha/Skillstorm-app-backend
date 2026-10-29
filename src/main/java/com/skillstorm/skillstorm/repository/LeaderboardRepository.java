package com.skillstorm.skillstorm.repository;

import com.skillstorm.skillstorm.model.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry,Integer> {

    // O(1) instant lookup
    @Query("SELECT l.rank FROM LeaderboardEntry l WHERE l.userId = :userId")
    Integer findRankByUserId(@Param("userId") int userId);
}
