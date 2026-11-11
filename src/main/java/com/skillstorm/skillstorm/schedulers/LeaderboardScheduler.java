
package com.skillstorm.skillstorm.schedulers;

import com.skillstorm.skillstorm.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaderboardScheduler {

    private final LeaderboardService leaderboardService;

    @Scheduled(fixedDelay = 60000)
    public void updateLeaderboard() {

        log.info("Executing scheduled leaderboard update...");

        leaderboardService.refreshLeaderboard();
        log.info("Updated leaderboard complete...");
    }
}
