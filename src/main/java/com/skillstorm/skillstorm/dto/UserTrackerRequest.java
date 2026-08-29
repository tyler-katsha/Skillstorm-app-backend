package com.skillstorm.skillstorm.dto;

import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTrackerRequest {

    private Integer userId;
    private Integer gainedXp;

    private Integer currentStreak;
    private Integer longestStreak;
    private Integer currentLevel;
    private Integer questionsAnswered;
    private Integer correctAnswers;
    private Long timeSpentSeconds;
    private Boolean isQuizWon;
    private Boolean isPerfectQuiz;

    private Integer completedQuizId;
    private Set<Integer> uncompletedQuizIds;
    private Set<Integer> bookmarkedQuizIds;
}
