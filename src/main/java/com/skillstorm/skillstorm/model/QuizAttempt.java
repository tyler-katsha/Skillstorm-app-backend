package com.skillstorm.skillstorm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    private Integer score;
    private Integer totalScore;
    private Boolean passed;
    private Integer durationSeconds;
    private LocalDateTime completedAt;

    @ElementCollection
    @CollectionTable(
            name = "quiz_attempt_questions",
            joinColumns = @JoinColumn(name = "quiz_attempt_id")
    )
    @Builder.Default
    private List<QuestionAttempt> questionAttempts = new ArrayList<>();
}