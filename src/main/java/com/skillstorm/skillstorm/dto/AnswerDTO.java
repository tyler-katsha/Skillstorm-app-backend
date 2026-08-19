package com.skillstorm.skillstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record AnswerDTO(String text, boolean isCorrect) {}
