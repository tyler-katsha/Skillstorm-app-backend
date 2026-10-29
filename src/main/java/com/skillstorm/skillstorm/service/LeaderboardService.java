package com.skillstorm.skillstorm.service;

import java.util.ArrayList;
import java.util.List;

import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.model.LeaderboardEntry;
import com.skillstorm.skillstorm.repository.LeaderboardRepository;
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

    private final LeaderboardRepository leaderboardRepository;


    public int getUserGlobalRank(Integer userId) {
        try {
            return leaderboardRepository.findRankByUserId(userId);
        } catch(Exception e) {
            return -1;
        }
    }

    @Cacheable(value = "top10Leaderboard", key = "'top10'")
    @Transactional(readOnly = true)
    public List<LeaderboardEntry> findAll() {
        log.info("Cache miss! Querying database for top 10 users by XP...");
        return leaderboardRepository.findAll();
    }

    @CacheEvict(value = "top10Leaderboard", allEntries = true)
    public void evictLeaderboardCache() {
        log.info("Leaderboard cache manually evicted.");
    }
}
