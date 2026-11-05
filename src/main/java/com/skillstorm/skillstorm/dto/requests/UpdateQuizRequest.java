package com.skillstorm.skillstorm.dto.requests;

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
    private List<UpdateQuestionRequest> questions;
}
