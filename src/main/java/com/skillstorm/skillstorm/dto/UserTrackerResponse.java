package com.skillstorm.skillstorm.dto;

import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.UncompletedQuizProgress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTrackerResponse {

    private Integer id;
    private Integer userId;
    private String username;

    // Flattened overall stats
    private Integer totalXp;
    private Integer xpToNewLevel;
    private Integer currentLevel;
    private Integer streak;
    private Integer longestStreak;
    private Integer quizzesTaken;
    private Integer quizzesWon;
    private Integer perfectQuizzes;
    private Integer totalQuestionsAnswered;
    private Integer totalCorrectAnswers;
    private Long totalTimeSpentSeconds;
    private LocalDate lastActiveDate;
    private Double accuracyRate;
    private Double winRate;

    // Associated Quiz IDs
    private Set<Integer> uncompletedQuizIds;
    private Set<Integer> completedQuizIds;
    private Set<Integer> bookmarkedQuizIds;

    // Attempt history overview
    private List<QuizAttemptResponse> attemptRecords;
    private Set<Quiz> completedQuizzes;
    private Set<UncompletedQuizProgress> uncompletedQuizzes;
    private Set<Quiz> bookmarkedQuizzes;
}
