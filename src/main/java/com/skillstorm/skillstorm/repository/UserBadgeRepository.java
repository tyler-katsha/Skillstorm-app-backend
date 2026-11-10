package com.skillstorm.skillstorm.repository;

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

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM UserBadge ub WHERE ub.badge.id = :badgeId")
    void deleteByBadgeId(@Param("badgeId") Integer badgeId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM UserBadge ub WHERE ub.user.id = :userId")
    void deleteByUserId(@Param("userId") int userId);
    Optional<UserBadge> findByUserAndBadge(User user, Badge badge);
    List<UserBadge> findByUser(User user);
}
