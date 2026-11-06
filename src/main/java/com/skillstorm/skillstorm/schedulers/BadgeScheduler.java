package com.skillstorm.skillstorm.schedulers;

import com.skillstorm.skillstorm.worker.UserBadgeUpdateWorker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BadgeScheduler {

    private final UserBadgeUpdateWorker badgeWorker;


    @Transactional
    @Scheduled(fixedDelay = 60000) // Runs every 60 seconds
    public void fetchAndUpdateBadgesForUsers(){
        log.info("Starting 30sec badge reconciliation...");

        badgeWorker.process(null);

        log.info("Badge reconciliation completed.");
    }

}
