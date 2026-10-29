package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.CreateQuizRequest;
import com.skillstorm.skillstorm.dto.QuizDTO;
import com.skillstorm.skillstorm.dto.QuizSummaryDto;
import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.Topic;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QuizMapperImpl implements QuizMapper {

    private final QuestionMapper questionMapper;

    public QuizMapperImpl(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    @Override
    public QuizDTO mapToDto(Quiz quiz) {

        if (quiz == null) {
            return null;
        }

        List<String> topicNames = getTopicsByName(quiz.getTopics());

        return new QuizDTO(
                quiz.getQuizId(),
                quiz.getTitle(),
                quiz.getDifficulty(),
                topicNames,
                questionMapper.mapToDto(quiz.getQuestions()));
    }

    @Override
    public Quiz toQuiz(CreateQuizRequest request) {

        if (request == null) {
            return null;
        }

        return Quiz.builder()
                .title(request.getTitle())
                .difficulty(request.getDifficulty())
                .build();
    }

    @Override
    public List<QuizSummaryDto> mapToSummaryDto(List<Quiz> quizzes) {

        if (quizzes == null || quizzes.isEmpty()) {
            return Collections.emptyList();
        }

        return quizzes.stream()
                .map(this::mapToSummaryDto)
                .toList();
    }

    @Override
    public QuizSummaryDto mapToSummaryDto(Quiz quiz) {

        if (quiz == null) {
            return null;
        }

        return QuizSummaryDto.builder()
                .id(quiz.getQuizId())
                .title(quiz.getTitle())
                .topicNames(getTopicsByName(quiz.getTopics()))
                .totalQuestions(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0)
                .build();
    }

    private List<String> getTopicsByName(List<Topic> topics) {

        if (topics == null || topics.isEmpty()) {
            return Collections.emptyList();
        }

        return topics.stream()
                .map(Topic::getName)
                .toList();
    }
}