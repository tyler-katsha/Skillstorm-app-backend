package com.skillstorm.skillstorm.dto;


import java.util.Set;

public record QuestionDTO(String text, int score, Set<AnswerDTO> answers) {}
