package com.skillstorm.skillstorm.dto;

import com.skillstorm.skillstorm.model.Question;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdateQuizRequest{
    private Integer id;
    private String title;
    private String difficulty;
    private List<String> topics;
    @Builder.Default
    private List<Question> questions = new ArrayList<>();
}
