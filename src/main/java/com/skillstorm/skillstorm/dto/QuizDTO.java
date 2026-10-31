package com.skillstorm.skillstorm.dto;

import com.skillstorm.skillstorm.enums.Difficulty;

import java.util.List;
import java.util.Set;

public record QuizDTO(int id, String title, Difficulty difficulty, List<String> topicNames, List<QuestionResponse> questions) {}
