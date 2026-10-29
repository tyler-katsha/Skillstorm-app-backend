package com.skillstorm.skillstorm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOverallStats {
    @Builder.Default
    @Column(name = "total_xp", nullable = false)
    private Integer totalXp = 0;

    @Builder.Default
    @Column(name = "current_level", nullable = false)
    private Integer currentLevel = 1;

    @Builder.Default
    @Column(name = "streak", nullable = false)
    private Integer currentStreak = 0;

    @Builder.Default
    @Column(name = "longest_streak", nullable = false)
    private Integer longestStreak = 0;

    @Builder.Default
    @Column(name = "quizzes_taken", nullable = false)
    private Integer totalQuizzesCompleted = 0;

    @Builder.Default
    @Column(name = "quizzes_won", nullable = false)
    private Integer quizzesWon = 0;

    @Builder.Default
    @Column(name = "perfect_quizzes", nullable = false)
    private Integer perfectQuizzes = 0;

    @Builder.Default
    @Column(name = "total_questions_answered", nullable = false)
    private Integer totalQuestionsAnswered = 0;

    @Builder.Default
    @Column(name = "total_correct_answers", nullable = false)
    private Integer totalCorrectAnswers = 0;

    @Builder.Default
    @Column(name = "total_time_spent_seconds", nullable = false)
    private Long totalTimeSpentSeconds = 0L;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    public Double getAccuracyRate() {
        if (totalQuestionsAnswered == null || totalQuestionsAnswered == 0) return 0.0;
        return ((double) totalCorrectAnswers / totalQuestionsAnswered) * 100.0;
    }

    public Double getWinRate() {
        if (totalQuizzesCompleted == null || totalQuizzesCompleted == 0) return 0.0;
        return ((double) quizzesWon / totalQuizzesCompleted) * 100.0;
    }
}
