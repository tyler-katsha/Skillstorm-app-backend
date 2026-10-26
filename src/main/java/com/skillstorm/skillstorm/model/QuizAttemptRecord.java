package com.skillstorm.skillstorm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "quiz_attempt_records")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttemptRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_tracker_id", nullable = false)
    private UserTracker userTracker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private Integer attemptNumber;

    private Integer questionsAnswered;
    private Integer correctAnswers;
    private Long timeSpentSeconds;
    private Integer gainedXp;
    private Boolean isQuizWon;
    private Boolean isPerfectQuiz;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Instant completedAt = Instant.now();
}
