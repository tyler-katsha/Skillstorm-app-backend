package com.skillstorm.skillstorm.schedulers;

import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.model.UserBadge;
import com.skillstorm.skillstorm.model.UserOverallStats;
import com.skillstorm.skillstorm.model.UserTracker;
import com.skillstorm.skillstorm.repository.BadgeRepository;
import com.skillstorm.skillstorm.repository.UserBadgeRepository;
import com.skillstorm.skillstorm.repository.UserTrackerRepository;
import com.skillstorm.skillstorm.utils.BadgeUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import static com.skillstorm.skillstorm.utils.TimeUtils.yesterday;


@Service
@Slf4j
@RequiredArgsConstructor
public class BadgeScheduler {

    private final UserTrackerRepository userTrackerRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeUtils utils;


    @Transactional
    @Scheduled(fixedDelay = 30000) // Runs every 30 seconds
    public void fetchAndUpdateBadgesForUsers(){
        log.info("Starting 30sec badge reconciliation...");

        List<Badge> allBadges = badgeRepository.findAll();


        List<UserTracker> allTrackers = userTrackerRepository.findUsersActiveSince(yesterday);


        for (UserTracker userTracker: allTrackers) {
            UserOverallStats stats = userTracker.getOverallStats();

            if(stats == null) continue;

            for (Badge badge: allBadges) {
                int actualProgress = utils.calculateProgressForKind(userTracker,badge);

                UserBadge userBadge = userBadgeRepository.findByUserAndBadge(userTracker.getUser(),badge)
                        .orElse(UserBadge.builder()
                                .currentProgress(0)
                                .isEarned(false)
                                .user(userTracker.getUser())
                                .badge(badge)
                                .build());


                if(!userBadge.isEarned() && actualProgress > userBadge.getCurrentProgress()) {
                    userBadge.setCurrentProgress(Math.min(actualProgress, badge.getGoal()));

                    if(userBadge.getCurrentProgress() >= badge.getGoal()) {
                        userBadge.setEarned(true);
                        userBadge.setEarnedAt(Instant.now());
                        log.info("User {} earned badge: {}",userTracker.getUser().getEmail(), badge.getTitle());
                    }

                    userBadgeRepository.save(userBadge);
                }
            }
        }
        log.info("Badge reconciliation complete.");
    }

}
