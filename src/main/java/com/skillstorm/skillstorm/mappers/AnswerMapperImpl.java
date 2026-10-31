package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.records.AnswerDTO;
import com.skillstorm.skillstorm.dto.responses.AnswerResponse;
import com.skillstorm.skillstorm.model.Answer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AnswerMapperImpl implements AnswerMapper {

    @Override
    public AnswerResponse toDto(Answer answer) {

        if (answer == null) {
            return null;
        }

        AnswerResponse response = new AnswerResponse();

        response.setAnswerId(answer.getAnswerId());
        response.setAnswerText(answer.getAnswerText());

        return response;
    }
}