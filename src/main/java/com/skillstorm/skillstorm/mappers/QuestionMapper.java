package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.QuestionDTO;
import com.skillstorm.skillstorm.model.Question;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel="spring")
public interface QuestionMapper {
    List<QuestionDTO> mapToDto(List<Question> questions);
}
