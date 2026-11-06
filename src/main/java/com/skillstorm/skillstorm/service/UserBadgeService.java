package com.skillstorm.skillstorm.service;

import com.skillstorm.skillstorm.model.*;
import com.skillstorm.skillstorm.repository.BadgeRepository;
import com.skillstorm.skillstorm.repository.UserBadgeRepository;
import com.skillstorm.skillstorm.repository.UserTrackerRepository;
import com.skillstorm.skillstorm.utils.BadgeUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.skillstorm.skillstorm.utils.TimeUtils.yesterday;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final UserTrackerRepository userTrackerRepository;
    private final BadgeUtils utils;

    @Transactional
    public UserBadge addProgress(User user, Badge badge, int progressAmount) {

        UserBadge userBadge = userBadgeRepository.findByUserAndBadge(user,badge)
                .orElse(UserBadge.builder()
                        .user(user)
                        .badge(badge)
                        .currentProgress(0)
                        .isEarned(false)
                        .build());

        userBadge.setCurrentProgress(userBadge.getCurrentProgress() + progressAmount);
        return userBadgeRepository.save(userBadge);
    }

    @Transactional
    public void initializeBadgesForUser(User user) {
        List<Badge> allBadges = badgeRepository.findAll();

        Set<Integer> existingBadgeIds = userBadgeRepository.findByUser(user)
                .stream()
                .map(userBadge -> userBadge.getBadge().getId())
                .collect(Collectors.toSet());

        List<UserBadge> newUserBadges = allBadges.stream()
                .filter(badge -> !existingBadgeIds.contains(badge.getId()))
                .map(badge -> UserBadge.builder()
                        .user(user)
                        .badge(badge)
                        .build())
                .collect(Collectors.toList());

        if (!newUserBadges.isEmpty()) {
            userBadgeRepository.saveAll(newUserBadges);
        }
    }
}
