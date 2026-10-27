package com.skillstorm.skillstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SubmissionPayload {
    private Integer quizId;
    private Integer userId;
    private Long timeSpentSeconds;
    private List<UserAnswerSubmission> answers;
}
