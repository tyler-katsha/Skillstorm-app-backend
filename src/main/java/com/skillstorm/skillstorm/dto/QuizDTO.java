package com.skillstorm.skillstorm.dto;

import java.util.List;
import java.util.Set;

public record QuizDTO(int id, String title, String difficulty, List<String> topicNames, List<QuestionResponse> questions) {}
