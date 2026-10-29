package com.skillstorm.skillstorm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "quiz_attempt_records")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"userTracker", "quiz"})
public class QuizAttemptRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuizAttemptRecord that = (QuizAttemptRecord) o;
        return Objects.equals(attemptId, that.attemptId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(attemptId);
    }
}