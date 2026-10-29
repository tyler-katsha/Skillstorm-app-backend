package com.skillstorm.skillstorm.dto;


import java.util.List;
import java.util.Set;

public record QuestionDTO(String text, int score, List<AnswerDTO> answers) {}
