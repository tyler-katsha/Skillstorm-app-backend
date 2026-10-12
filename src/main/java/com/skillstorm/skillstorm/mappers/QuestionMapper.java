package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.CreateQuestionRequest;
import com.skillstorm.skillstorm.dto.QuestionDTO;
import com.skillstorm.skillstorm.dto.QuestionResponse;
import com.skillstorm.skillstorm.dto.QuizSummaryDto;
import com.skillstorm.skillstorm.model.Question;
import com.skillstorm.skillstorm.model.Quiz;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel="spring")
public interface QuestionMapper {
    QuestionResponse toDto(Question question);
    Set<QuestionResponse> mapToDto(Set<Question> questions);
    Question toQuestion(CreateQuestionRequest request);
    Set<Question> toQuestion(Set<CreateQuestionRequest> questions);
}