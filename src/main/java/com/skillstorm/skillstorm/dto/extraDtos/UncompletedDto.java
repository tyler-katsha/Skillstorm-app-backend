package com.skillstorm.skillstorm.dto.extraDtos;

import com.skillstorm.skillstorm.model.Quiz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UncompletedDto {
    private int currentQuestion;
    private int totalQuestions;
    private int percentage;
    private UserAnswerSubmission answerSubmission;
}
