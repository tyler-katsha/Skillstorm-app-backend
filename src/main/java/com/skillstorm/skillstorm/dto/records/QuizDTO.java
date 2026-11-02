package com.skillstorm.skillstorm.dto.records;

import com.skillstorm.skillstorm.dto.responses.QuestionResponse;
import com.skillstorm.skillstorm.enums.Difficulty;

import java.util.List;

public record QuizDTO(int id, String title, Difficulty difficulty, List<String> topicNames, List<QuestionResponse> questions) {}
