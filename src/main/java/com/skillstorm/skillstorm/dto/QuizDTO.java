package com.skillstorm.skillstorm.dto;

import java.util.Set;

public record QuizDTO(String title, String difficulty, Set<String> topicNames, Set<QuestionDTO> questions) {}
