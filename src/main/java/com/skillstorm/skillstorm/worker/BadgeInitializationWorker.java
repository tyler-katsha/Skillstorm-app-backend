package com.skillstorm.skillstorm.worker;

import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;
import com.skillstorm.skillstorm.service.UserBadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BadgeInitializationWorker implements BackgroundWorker<Integer>{

    private final UserRepository userRepository;
    private final UserBadgeService userBadgeService;

    @Override
    public void process(Integer val) {
        int pageNumber = 0;
        int pageSize = 100; // Process 100 users at a time to save RAM
        Page<User> userPage;

        do {
            userPage = userRepository.findAll(PageRequest.of(pageNumber,pageSize));

            for (User user: userPage.getContent()) {
                userBadgeService.initializeBadgesForUser(user);
            }
            pageNumber++;
        } while(userPage.hasNext());

        log.info("Background worker finished initializing badge for all users.");
    }
}
