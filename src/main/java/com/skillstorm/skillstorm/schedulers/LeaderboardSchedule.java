package com.skillstorm.skillstorm.schedulers;

import com.skillstorm.skillstorm.model.LeaderboardEntry;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.LeaderboardRepository;
import com.skillstorm.skillstorm.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardSchedule {

    private final UserService userService;
    private final LeaderboardRepository leaderboardRepository;

    @CacheEvict(value = "top10Leaderboard", allEntries = true)
    @Transactional
    @Scheduled(cron = " 0 */5 * * * *") // Schedules an update every 5 minutes
    public void updateLeaderboard(){

        log.trace("Executing Global Leaderboard updates....");

        List<User> allUsers = userService.findAllByOrderByXpDesc();

        if(allUsers == null || allUsers.isEmpty()){
            log.warn("No users found to populate the leaderboard");
            return;
        }

        // clears old entries and updates database with fresh data
        leaderboardRepository.deleteAllInBatch();

        List<LeaderboardEntry> entries = new ArrayList<>();

        int currentRank = -1;
        int previousXp = -1;

        for(int i = 0; i < allUsers.size();i++){


            var user = allUsers.get(i);

            if(i == 0 || user.getXp() < previousXp) {
                currentRank = i + 1;
            }

            var entry = LeaderboardEntry.builder()
                    .rank(currentRank)
                    .xp(user.getXp())
                    .level(user.getLevel())
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .build();

            entries.add(entry);
            previousXp = user.getXp();
        }

        // if we have 100K+ users we can implement batch updates instead of saving all at once
        leaderboardRepository.saveAll(entries);
    }
}