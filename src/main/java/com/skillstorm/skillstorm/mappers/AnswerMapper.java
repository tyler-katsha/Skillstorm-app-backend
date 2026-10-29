package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.AnswerDTO;
import com.skillstorm.skillstorm.dto.AnswerResponse;
import com.skillstorm.skillstorm.model.Answer;

import java.util.List;

public interface AnswerMapper {

    AnswerResponse toDto(Answer answer);

    List<AnswerResponse> mapToDto(List<Answer> answers);

    List<AnswerDTO> mapToAnswerDTO(List<Answer> answers);
}