package com.skillstorm.skillstorm.service;

import java.util.ArrayList;
import java.util.List;

import com.skillstorm.skillstorm.dto.LeaderboardEntryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    @Cacheable(value = "top10Leaderboard", key = "'top10'")
    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getTop10Leaderboard() {
        log.info("Cache miss! Querying database for top 10 users by XP...");

        List<User> topUsers = userRepository.findTop10ByOrderByXpDesc();
        List<LeaderboardEntryDto> entries = new ArrayList<>();

        for (int i = 0; i < topUsers.size(); i++) {
            User user = topUsers.get(i);
            entries.add(LeaderboardEntryDto.builder()
                    .rank(i + 1)
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .xp(user.getXp() != null ? user.getXp() : 0)
                    .level(user.getLevel() != null ? user.getLevel() : 1)
                    .build());
        }

        return entries;
    }

    /**
     * Optional manual eviction if you ever want immediate invalidation
     * (e.g., after an admin reset or milestone event).
     */
    @CacheEvict(value = "top10Leaderboard", allEntries = true)
    public void evictLeaderboardCache() {
        log.info("Leaderboard cache manually evicted.");
    }
}
