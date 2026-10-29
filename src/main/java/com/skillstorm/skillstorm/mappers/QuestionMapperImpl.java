package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.AnswerResponse;
import com.skillstorm.skillstorm.dto.CreateQuestionRequest;
import com.skillstorm.skillstorm.dto.QuestionResponse;
import com.skillstorm.skillstorm.model.Answer;
import com.skillstorm.skillstorm.model.Question;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
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
    public List<QuestionResponse> mapToDto(List<Question> questions) {

        if (questions == null || questions.isEmpty()) {
            return Collections.emptyList();
        }

        return questions.stream()
                .map(this::toDto)
                .toList();
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
    public List<Question> toQuestion(List<CreateQuestionRequest> questions) {

        if (questions == null || questions.isEmpty()) {
            return Collections.emptyList();
        }

        return questions.stream()
                .map(this::toQuestion)
                .toList();
    }

    private AnswerResponse mapAnswer(Answer answer) {

        if (answer == null) {
            return null;
        }

        AnswerResponse response = new AnswerResponse();

        response.setAnswerId(answer.getAnswerId());
        response.setAnswerText(answer.getAnswerText());
        response.setIsCorrect(answer.isCorrect());

        return response;
    }
}