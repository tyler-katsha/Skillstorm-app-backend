package com.skillstorm.skillstorm.dto;

import java.util.List;

public record QuizDTO(String title, String difficulty, List<String> topicNames, List<QuestionDTO> questions) {}
