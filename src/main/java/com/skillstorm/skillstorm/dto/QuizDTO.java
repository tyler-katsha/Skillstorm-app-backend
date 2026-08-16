package com.skillstorm.skillstorm.dto;

import java.util.List;

record AnswerDTO(String text, boolean isCorrect) {}

record QuestionDTO(String text, int score, List<AnswerDTO> answers) {}

public record QuizDTO(String title, String difficulty, List<String> topicNames, List<QuestionDTO> questions) {}
