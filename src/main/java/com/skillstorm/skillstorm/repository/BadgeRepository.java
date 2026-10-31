package com.skillstorm.skillstorm.repository;

import com.skillstorm.skillstorm.dto.BadgeResponse;
import com.skillstorm.skillstorm.dto.SmallBadgeResponse;
import com.skillstorm.skillstorm.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Integer> {

    @Query("""
        SELECT new com.skillstorm.skillstorm.dto.SmallBadgeResponse(b.title, b.definitionId, b.icon)
        FROM Badge b
        ORDER BY rand()
        FETCH FIRST 4 ROWS ONLY
    """)
    List<SmallBadgeResponse> find4BadgesToDisplay();


    @Query("""
        SELECT new com.skillstorm.skillstorm.dto.BadgeResponse(b.id, b.title, b.description, b.icon, b.goal, ub.currentProgress)
        FROM UserBadge ub
        JOIN ub.badge b
        WHERE ub.user.id = :userId AND ub.isEarned = false
        ORDER BY (b.goal - ub.currentProgress) ASC
        FETCH FIRST 1 ROW ONLY
    """)
    BadgeResponse findNextBadgeToUnlock(@Param("userId") int userId);
}