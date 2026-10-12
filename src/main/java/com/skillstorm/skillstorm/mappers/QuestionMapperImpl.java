package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.AnswerResponse;
import com.skillstorm.skillstorm.dto.CreateQuestionRequest;
import com.skillstorm.skillstorm.dto.QuestionResponse;
import com.skillstorm.skillstorm.model.Answer;
import com.skillstorm.skillstorm.model.Question;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QuestionMapperImpl implements QuestionMapper {

    @Override
    public QuestionResponse toDto(Question question) {

        if (question == null) {
            return null;
        }

        QuestionResponse response = new QuestionResponse();

        response.setQuestionId(question.getQuestionId());
        response.setText(question.getText());
        response.setScore(question.getScore());

        if (question.getAnswers() != null) {
            response.setAnswers(
                    question.getAnswers()
                            .stream()
                            .map(this::mapAnswer)
                            .toList()
            );
        } else {
            response.setAnswers(Collections.emptyList());
        }

        return response;
    }

    @Override
    public Set<QuestionResponse> mapToDto(Set<Question> questions) {

        if (questions == null || questions.isEmpty()) {
            return Collections.emptySet();
        }

        return questions.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Question toQuestion(CreateQuestionRequest request) {

        if (request == null) {
            return null;
        }

        return Question.builder()
                .text(request.getText())
                .score(1)
                .build();
    }

    @Override
    public Set<Question> toQuestion(
            Set<CreateQuestionRequest> questions
    ) {

        if (questions == null || questions.isEmpty()) {
            return Collections.emptySet();
        }

        return questions.stream()
                .map(this::toQuestion)
                .collect(Collectors.toSet());
    }

    private AnswerResponse mapAnswer(Answer answer) {

        if (answer == null) {
            return null;
        }

        AnswerResponse response = new AnswerResponse();

        response.setAnswerId(answer.getAnswerId());
        response.setAnswerText(answer.getAnswerText());

        return response;
    }
}