package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.AnswerDTO;
import com.skillstorm.skillstorm.dto.QuestionDTO;
import com.skillstorm.skillstorm.dto.QuizSummaryDto;
import com.skillstorm.skillstorm.model.Question;
import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QuestionMapperImpl implements QuestionMapper{

    @Autowired
    private AnswerMapper answerMapper;

    @Override
    public Set<QuestionDTO> mapToDto(Set<Question> questions) {
        return questions.stream()
                .map(question -> {

                    Set<AnswerDTO> dtos = answerMapper.mapToDto(question.getAnswers());
                    return new QuestionDTO(question.getText(), question.getScore(), dtos);
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Set<QuizSummaryDto> mapToSummaryDto(Set<Question> questions) {


        return questions.stream()
                .map(question -> {
                    Quiz quiz = question.getQuiz();
                    if (quiz == null) {
                        return null; // Or throw IllegalArgumentException if strict non-null is intentional
                    }
                    return QuizSummaryDto.builder()
                            .id(question.getQuestionId())
                            .title(question.getText())
                            .topicNames(getTopicsByName(quiz.getTopics()))
                            .totalQuestions(questions.size())
                            .build();
                })
                .collect(Collectors.toSet());
    }

    private List<String> getTopicsByName(Set<Topic> topics) {
        if (topics == null || topics.isEmpty()) {
            return Collections.emptyList();
        }

        return topics.stream()
                .map(Topic::getName)
                .toList();
    }
}