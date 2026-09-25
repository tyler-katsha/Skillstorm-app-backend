package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.QuestionDTO;
import com.skillstorm.skillstorm.dto.QuizSummaryDto;
import com.skillstorm.skillstorm.model.Question;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel="spring")
public interface QuestionMapper {
    Set<QuestionDTO> mapToDto(Set<Question> questions);
    Set<QuizSummaryDto> mapToSummaryDto(Set<Question> questions);
}