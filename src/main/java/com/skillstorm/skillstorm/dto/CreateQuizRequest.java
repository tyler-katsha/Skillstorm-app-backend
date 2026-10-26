package com.skillstorm.skillstorm.dto;

import com.skillstorm.skillstorm.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizRequest {
    private String title;
    private String difficulty;
    private List<String> topicNames;
    private List<CreateQuestionRequest> questions;
}
