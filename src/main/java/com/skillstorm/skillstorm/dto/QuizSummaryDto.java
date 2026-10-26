package com.skillstorm.skillstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizSummaryDto {

    private Integer id;
    private String title;
    private List<String> topicNames;
    private Integer totalQuestions;
}
