package com.skillstorm.skillstorm.repository;

import com.skillstorm.skillstorm.model.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry,Integer> {

    // O(1) instant lookup
    @Query("SELECT l.rank FROM LeaderboardEntry l WHERE l.userId = :userId")
    Integer findRankByUserId(@Param("userId") int userId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " + "FROM LeaderboardEntry l " + " WHERE l.userId = :userId AND l.rank = 1")
    Boolean isGlobalChampion(@Param("userId") int userId);

    @Query(
            value = "WITH RankedUsers AS (" +
                    "  SELECT user_id, PERCENT_RANK() OVER (ORDER BY xp DESC) AS p_rank " +
                    "  FROM leaderboard" +
                    ") " +
                    "SELECT CASE WHEN p_rank <= 0.10 THEN TRUE ELSE FALSE END " +
                    "FROM RankedUsers " +
                    "WHERE user_id = :userId",
            nativeQuery = true
    )
    Long checkUserInTop10Percent(@Param("userId") int userId);

    default Boolean isUserInTop10TenPercentDynamic(int userId) {
        Long result = checkUserInTop10Percent(userId);
        return result != null && result == 1L;
    }
}
