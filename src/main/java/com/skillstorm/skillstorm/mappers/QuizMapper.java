package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.QuizDTO;
import com.skillstorm.skillstorm.model.Quiz;
import org.mapstruct.Mapper;

@Mapper
public interface QuizMapper {
    QuizDTO mapToDto(Quiz quiz);
}
