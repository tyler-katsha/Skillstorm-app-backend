package com.skillstorm.skillstorm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_attempt_questions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private QuizAttempt quizAttempt;

    private Long questionId;
    private Integer questionIndex;

    @Column(length = 1000)
    private String questionText;

    @Column(length = 500)
    private String selectedAnswer;

    @Column(length = 500)
    private String correctAnswer;

    @Column(nullable = false)
    private boolean correct;

    @Column(nullable = false)
    private int pointsEarned;

    @Column(nullable = false)
    private long timeSpentSeconds;
}