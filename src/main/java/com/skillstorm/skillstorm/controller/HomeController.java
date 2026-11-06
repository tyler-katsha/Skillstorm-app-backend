package com.skillstorm.skillstorm.controller;

import com.skillstorm.skillstorm.annotations.RateLimited;
import com.skillstorm.skillstorm.dto.extraDtos.HomeSection;
import com.skillstorm.skillstorm.dto.responses.SmallBadgeResponse;
import com.skillstorm.skillstorm.dto.responses.SmallQuizResponse;
import com.skillstorm.skillstorm.exceptions.RateLimitExceededException;
import com.skillstorm.skillstorm.service.BadgeService;
import com.skillstorm.skillstorm.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.temporal.ChronoUnit;
import java.util.List;


@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final TopicService topicService;
    private final BadgeService badgeService;

    @GetMapping
    @RateLimited(capacity = 50, tokens = 70, unit = ChronoUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public ResponseEntity<HomeSection> findHomePageData(){

        try {
            List<SmallBadgeResponse> badgeSection = badgeService.findBadgesToDisplay();
            List<SmallQuizResponse> quizSection =  topicService.findTopTopicsWithQuizCountAndDifficulty();

            HomeSection sections = HomeSection.builder()
                    .badgeSection(badgeSection)
                    .quizSection(quizSection)
                    .build();

            return ResponseEntity.ok(sections);
        } catch (RateLimitExceededException e){
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
