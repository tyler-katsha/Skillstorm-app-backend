package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.AnswerDTO;
import com.skillstorm.skillstorm.dto.QuestionDTO;
import com.skillstorm.skillstorm.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionMapperImpl implements QuestionMapper{

    @Autowired
    private AnswerMapper answerMapper;

    @Override
    public List<QuestionDTO> mapToDto(List<Question> questions) {
        return questions.stream()
                .map(question -> {

                    List<AnswerDTO> dtos = answerMapper.mapToDto(question.getAnswers());
                    return new QuestionDTO(question.getText(), question.getScore(), dtos);
                })
                .toList();
    }
}
