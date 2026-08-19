package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.AnswerDTO;
import com.skillstorm.skillstorm.model.Answer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnswerMapperImpl implements AnswerMapper{


    @Override
    public List<AnswerDTO> mapToDto(List<Answer> answers) {
        return answers.stream()
                .map( answer -> new AnswerDTO(answer.getAnswerText(), answer.isCorrect()))
                .toList();
    }
}
