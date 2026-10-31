package com.skillstorm.skillstorm.dto.payloads;

import com.skillstorm.skillstorm.dto.extraDtos.UncompletedDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizUncompletedPayload {
    private UncompletedDto uncompletedDto;
    private int quizId;
    private int userId;
}
