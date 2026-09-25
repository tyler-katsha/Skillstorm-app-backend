package com.skillstorm.skillstorm.schedulers;

import com.skillstorm.skillstorm.model.Leaderboard;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.LeaderboardRepository;
import com.skillstorm.skillstorm.service.LeaderboardService;
import com.skillstorm.skillstorm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardSchedule {

    private final LeaderboardService leaderboardService;
    private final UserService userService;
    private final LeaderboardRepository leaderboardRepository;

    @Retryable(maxRetries = 3,multiplier = 2,delay = 3000) // Retries 3 times before throwing an exception and the first delay is 3secs,then 6secs, then 12secs
    @Scheduled(cron = " 0 */1 * * * *") // Schedules an update every minute
    public void updateLeaderboard(){

        log.trace("Executing Leaderboard updates....");

        List<User> topUsers = userService.findTop10ByOrderByXpDesc();

        if(topUsers == null || topUsers.isEmpty()){
            log.warn("No users found to populate the leaderboard");
            return;
        }

        // clears old enteries and updates database with fresh data
        leaderboardRepository.deleteAllInBatch();

        List<Leaderboard> entries = new ArrayList<>();
        for(int i = 0; i < topUsers.size();i++){


            var user = topUsers.get(i);

            var entry = Leaderboard.builder()
                    .rank(i + 1)
                    .user(user)
                    .totalScore(user.getXp())
                    .build();

            entries.add(entry);

        }

        leaderboardRepository.saveAll(entries);
    }
}
