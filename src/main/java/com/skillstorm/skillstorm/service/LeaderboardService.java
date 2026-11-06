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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;


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

    @Transactional
    @CacheEvict(value = "top10Leaderboard", allEntries = true)
    public void refreshLeaderboard() {

        log.info("Refreshing global leaderboard...");

        List<User> users = userRepository.findAllByOrderByXpDesc();

        leaderboardRepository.deleteAllInBatch();

        if (users.isEmpty()) {
            log.warn("No users found to populate leaderboard.");
            return;
        }

        List<LeaderboardEntry> entries = new ArrayList<>();

        int currentRank = 1;
        int previousXp = -1;

        for (int i = 0; i < users.size(); i++) {

            User user = users.get(i);

            if (i == 0 || user.getXp() < previousXp) {
                currentRank = i + 1;
            }

            LeaderboardEntry entry = LeaderboardEntry.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .xp(user.getXp())
                    .level(user.getLevel())
                    .rank(currentRank)
                    .build();

            entries.add(entry);

            previousXp = user.getXp();
        }

        leaderboardRepository.saveAll(entries);
        leaderboardRepository.flush();

        log.info("Leaderboard successfully refreshed with {} users.", entries.size());
    }

    public boolean isUserInTop10TenPercentDynamic(int userId) {

        int rank = getUserGlobalRank(userId);
        long totalUsers = leaderboardRepository.count();

        if(rank == -1 || totalUsers == 0) return false;

        int topTenPercent = (int) Math.ceil(totalUsers * 0.10);

        return rank <= topTenPercent;
    }

    public boolean isGlobalChampion(int userId) {
        return getUserGlobalRank(userId) == 1;
    }
}
