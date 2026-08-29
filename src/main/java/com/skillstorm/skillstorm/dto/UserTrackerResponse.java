package com.skillstorm.skillstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTrackerResponse {

    private Integer id;
    private Integer userId;
    private String username;

    private Integer currentLevel;
    private Integer totalXp;
    private Integer xpToNewLevel;

    private Integer streak;
    private Integer longestStreak;
    private LocalDate lastActiveDate;
    private Long totalTimeSpentSeconds;

    private Integer quizzesTaken;
    private Integer quizzesWon;
    private Integer quizzesLost;
    private Integer drawn;
    private Integer perfectQuizzes;
    private Double winRate;

    private Integer totalQuestionsAnswered;
    private Integer totalCorrectAnswers;
    private Double accuraryRate;

    private Set<QuizSummaryDto> completedQuizzes;
    private Set<QuizSummaryDto> uncompletedQuizzes;
    private Set<QuizSummaryDto> bookmarkedQuizzes;
}
