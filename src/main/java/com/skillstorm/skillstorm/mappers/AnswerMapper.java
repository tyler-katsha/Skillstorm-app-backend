package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.AnswerDTO;
import com.skillstorm.skillstorm.dto.AnswerResponse;
import com.skillstorm.skillstorm.model.Answer;

import java.util.List;
import java.util.Set;

public interface AnswerMapper {

    AnswerResponse toDto(Answer answer);

    List<AnswerResponse> mapToDto(Set<Answer> answers);

    Set<AnswerDTO> mapToAnswerDTO(Set<Answer> answers);
}