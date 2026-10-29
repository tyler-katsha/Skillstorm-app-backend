package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.AnswerDTO;
import com.skillstorm.skillstorm.dto.AnswerResponse;
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

    @Override
    public List<AnswerResponse> mapToDto(List<Answer> answers) {

        if (answers == null || answers.isEmpty()) {
            return Collections.emptyList();
        }

        return answers.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<AnswerDTO> mapToAnswerDTO(List<Answer> answers) {

        if (answers == null || answers.isEmpty()) {
            return Collections.emptyList();
        }

        return answers.stream()
                .map(answer -> new AnswerDTO(
                        answer.getAnswerText(),
                        answer.isCorrect()
                ))
                .toList();
    }
}