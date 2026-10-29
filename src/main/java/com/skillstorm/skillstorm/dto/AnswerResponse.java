package com.skillstorm.skillstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponse {
    private Integer answerId;
    private String answerText;
    private Boolean isCorrect;
}
