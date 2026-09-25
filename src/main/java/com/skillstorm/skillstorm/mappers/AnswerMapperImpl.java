package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.AnswerDTO;
import com.skillstorm.skillstorm.model.Answer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AnswerMapperImpl implements AnswerMapper{


    @Override
    public Set<AnswerDTO> mapToDto(Set<Answer> answers) {
        return answers.stream()
                .map( answer -> new AnswerDTO(answer.getAnswerText(), answer.isCorrect()))
                .collect(Collectors.toSet());
    }
}
