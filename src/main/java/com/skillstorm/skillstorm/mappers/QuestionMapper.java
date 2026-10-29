package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.CreateQuestionRequest;
import com.skillstorm.skillstorm.dto.QuestionResponse;
import com.skillstorm.skillstorm.model.Question;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel="spring")
public interface QuestionMapper {
    QuestionResponse toDto(Question question);
    List<QuestionResponse> mapToDto(List<Question> questions);
    Question toQuestion(CreateQuestionRequest request);
    List<Question> toQuestion(List<CreateQuestionRequest> questions);
}