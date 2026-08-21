package com.skillstorm.skillstorm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAttempt {

    private int correctQuestion;
    private long timeTakenPerQuestion;
    private String questionText;
    private String selectedAnswer;
    private String correctAnswer;
    private boolean isCorrect;
    private int pointsEarned;
}
