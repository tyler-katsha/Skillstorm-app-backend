package com.skillstorm.skillstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserAnswerSubmission {
    private int questionId;
    private int selectedAnswerId;
}
