package com.skillstorm.skillstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponse {
    private Integer quizId;
    private String title;
    private String difficulty;
    private List<String> topicNames;
    private List<QuestionResponse> questions;
}
