package com.skillstorm.skillstorm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "uncompleted_quiz_progress")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UncompletedQuizProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_tracker_id", nullable = false)
    private UserTracker userTracker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "current_question", nullable = false)
    private int currentQuestion;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private int percentage;

    @Builder.Default
    @Column(name = "last_saved_at", nullable = false)
    private Instant lastSavedAt = Instant.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UncompletedQuizProgress that = (UncompletedQuizProgress) o;
        return Objects.equals(userTracker != null ? userTracker.getId() : null, that.userTracker != null ? that.userTracker.getId() : null)
                && Objects.equals(quiz != null ? quiz.getQuizId() : null, that.quiz != null ? that.quiz.getQuizId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                userTracker != null ? userTracker.getId() : null,
                quiz != null ? quiz.getQuizId() : null
        );
    }
}