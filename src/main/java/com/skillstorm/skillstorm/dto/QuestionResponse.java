package com.skillstorm.skillstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {

    private Integer questionId;
    private String text;
    private int score;
    private List<AnswerResponse> answers;
}
