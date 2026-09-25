package com.skillstorm.skillstorm.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<QuestionAttempt> questions = new HashSet<>();

    // Helper method to keep bidirectional state in sync
    public void addQuestionAttempt(QuestionAttempt questionAttempt) {
        questions.add(questionAttempt);
        questionAttempt.setQuizAttempt(this);
    }

    public void removeQuestionAttempt(QuestionAttempt questionAttempt) {
        questions.remove(questionAttempt);
        questionAttempt.setQuizAttempt(null);
    }
}