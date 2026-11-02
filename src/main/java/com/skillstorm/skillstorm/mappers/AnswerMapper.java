package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.records.AnswerDTO;
import com.skillstorm.skillstorm.dto.responses.AnswerResponse;
import com.skillstorm.skillstorm.model.Answer;

import java.util.List;

public interface AnswerMapper {

    AnswerResponse toDto(Answer answer);
}