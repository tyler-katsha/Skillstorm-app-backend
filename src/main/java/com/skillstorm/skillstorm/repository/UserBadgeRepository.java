package com.skillstorm.skillstorm.repository;

import com.skillstorm.skillstorm.dto.responses.BadgeResponse;
import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge,Integer> {

    @Modifying
    @Query("DELETE FROM UserBadge ub WHERE ub.badge.id = :badgeId")
    void deleteByBadgeId(@Param("badgeId") Integer badgeId);
    Optional<UserBadge> findByUserAndBadge(User user, Badge badge);
    List<UserBadge> findByUser(User user);

    @Query("""
           SELECT new com.skillstorm.skillstorm.dto.responses.BadgeResponse(
                b.id,b.definitionId, b.title,b.description, b.icon, b.kind,
                b.goal, ub.currentProgress, ub.isEarned, ub.earnedAt
           ) FROM UserBadge ub
           JOIN ub.badge b
           WHERE ub.user.id = :userId
           """)
    List<BadgeResponse> findAllBadgesForUser(@Param("userId") int userId);
}
