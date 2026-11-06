package com.skillstorm.skillstorm.worker;

import com.skillstorm.skillstorm.enums.Kind;
import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.model.UserBadge;
import com.skillstorm.skillstorm.model.UserTracker;
import com.skillstorm.skillstorm.model.UserOverallStats;
import com.skillstorm.skillstorm.repository.BadgeRepository;
import com.skillstorm.skillstorm.repository.UserBadgeRepository;
import com.skillstorm.skillstorm.repository.UserTrackerRepository;
import com.skillstorm.skillstorm.utils.BadgeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserBadgeUpdateWorker implements BackgroundWorker<Void> {

    private final UserTrackerRepository userTrackerRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeUtils utils;

    @Override
    public void process(Void val) {

        int pageNumber = 0;
        int pageSize = 100;

        List<Badge> allBadges = badgeRepository.findAll()
                .stream()
                .filter(b -> b.getKind() != Kind.TOP_10 && b.getKind() != Kind.GLOBAL_CHAMP && b.getKind() != Kind.PODIUM_FINISH)
                .toList();

        Page<UserTracker> userPage;

        do {

            userPage = userTrackerRepository.findAll(PageRequest.of(pageNumber, pageSize));

            for (UserTracker userTracker : userPage.getContent()) {

                UserOverallStats stats = userTracker.getOverallStats();

                if (stats == null) {
                    log.warn("Skipping user {} because overall stats are null", userTracker.getUser());
                    continue;
                }

                for (Badge badge : allBadges) {

                    int actualProgress = utils.calculateProgressForKind(userTracker, badge);

                    UserBadge userBadge = userBadgeRepository
                            .findByUserAndBadge(userTracker.getUser(), badge)
                            .orElse(null);

                    if(userBadge == null) {

                        int progress = Math.min(actualProgress,badge.getGoal());
                        boolean earned = progress >= badge.getGoal();

                        userBadge = UserBadge.builder()
                                .currentProgress(progress)
                                .isEarned(earned)
                                .earnedAt(earned ? Instant.now() : null)
                                .user(userTracker.getUser())
                                .badge(badge)
                                .build();

                        userBadgeRepository.save(userBadge);

                        log.info("BADGE DEBUG: UserBadge saved for {} - {}",
                                userTracker.getUser().getEmail(),
                                badge.getTitle());

                        continue;
                    }

                    if (!userBadge.isEarned() && actualProgress > userBadge.getCurrentProgress()) {

                        userBadge.setCurrentProgress(Math.min(actualProgress, badge.getGoal()));

                        if (userBadge.getCurrentProgress() >= badge.getGoal()) {

                            userBadge.setEarned(true);
                            userBadge.setEarnedAt(Instant.now());

                            log.info("User {} earned badge: {}", userTracker.getUser().getEmail(), badge.getTitle());
                        }

                        userBadgeRepository.save(userBadge);
                    }
                }
            }

            pageNumber++;

        } while (userPage.hasNext());

        log.info("Badge worker finished processing users.");
    }
}