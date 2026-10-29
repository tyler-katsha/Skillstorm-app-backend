package com.skillstorm.skillstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttemptResponse {
    private Long attemptId;
    private Integer quizId;
    private Integer attemptNumber;
    private Integer questionsAnswered;
    private Integer correctAnswers;
    private Long timeSpentSeconds;
    private Integer gainedXp;
    private Boolean isQuizWon;
    private Boolean isPerfectQuiz;
    private Instant completedAt;
}
