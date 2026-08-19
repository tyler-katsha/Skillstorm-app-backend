package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.AnswerDTO;
import com.skillstorm.skillstorm.model.Answer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface AnswerMapper {
    List<AnswerDTO> mapToDto(List<Answer> answers);
}
