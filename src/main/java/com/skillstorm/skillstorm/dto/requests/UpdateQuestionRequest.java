package com.skillstorm.skillstorm.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdateQuestionRequest {
    private Integer id;
    private String text;
    private Integer score;
    private String description;
    private List<String> options;
    private Integer correctOption;
}
