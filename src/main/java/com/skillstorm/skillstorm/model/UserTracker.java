package com.skillstorm.skillstorm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_tracker")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // --- Quiz Performance ---
    @Builder.Default
    @Column(nullable = false)
    private Integer quizzesTaken = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer quizzesWon = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer perfectQuizzes = 0;

    // --- Question-Level Metrics ---
    @Builder.Default
    @Column(nullable = false)
    private Integer totalQuestionsAnswered = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalCorrectAnswers = 0;

    // --- Streaks & Activity ---
    @Builder.Default
    @Column(nullable = false)
    private Integer streak = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer longestStreak = 0;

    private LocalDate lastActiveDate;

    @Builder.Default
    @Column(nullable = false)
    private Long totalTimeSpentSeconds = 0L;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalXp = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer currentLevel = 1;

    // --- Quiz Relationships (Using Sets to avoid duplicates) ---
    @ManyToMany
    @JoinTable(
            name = "user_tracker_uncompleted_quizzes",
            joinColumns = @JoinColumn(name = "user_tracker_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @Builder.Default
    private Set<Quiz> uncompletedQuizzes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_tracker_completed_quizzes",
            joinColumns = @JoinColumn(name = "user_tracker_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @Builder.Default
    private Set<Quiz> completedQuizzes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_tracker_bookmarked_quizzes",
            joinColumns = @JoinColumn(name = "user_tracker_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @Builder.Default
    private Set<Quiz> bookmarkedQuizzes = new HashSet<>();

    // --- Computed Helper Methods ---
    @Transient
    public Double getAccuracyRate() {
        if (totalQuestionsAnswered == 0) return 0.0;
        return ((double) totalCorrectAnswers / totalQuestionsAnswered) * 100.0;
    }

    @Transient
    public Double getWinRate() {
        if (quizzesTaken == 0) return 0.0;
        return ((double) quizzesWon / quizzesTaken) * 100.0;
    }
}