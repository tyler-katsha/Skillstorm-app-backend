package com.skillstorm.skillstorm.dto.extraDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionResultDto {
    private int scoreEarned;
    private int correctCount;
    private int totalQuestions;
    private int currentXp;
    private int currentLevel;
    private boolean leveledUp;
}
