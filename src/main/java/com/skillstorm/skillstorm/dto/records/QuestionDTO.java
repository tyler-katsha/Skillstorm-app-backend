package com.skillstorm.skillstorm.dto.records;


import java.util.List;

public record QuestionDTO(String text, int score, List<AnswerDTO> answers) {}
