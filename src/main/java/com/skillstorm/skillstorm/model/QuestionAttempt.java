package com.skillstorm.skillstorm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAttempt {

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