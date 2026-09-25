package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.QuestionDTO;
import com.skillstorm.skillstorm.dto.QuizDTO;
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
            return null; // Or throw IllegalArgumentException if strict non-null is intentional
        }

        Set<String> topicNames = getTopicsByName(quiz.getTopics());
        Set<QuestionDTO> questionDtos = (quiz.getQuestions() != null)
                ? questionMapper.mapToDto(quiz.getQuestions())
                : Collections.emptySet();

        return new QuizDTO(
                quiz.getTitle(),
                quiz.getDifficulty(),
                topicNames,
                questionDtos
        );
    }

    private Set<String> getTopicsByName(Set<Topic> topics) {
        if (topics == null || topics.isEmpty()) {
            return Collections.emptySet();
        }

        return topics.stream()
                .map(Topic::getName)
                .collect(Collectors.toSet());
    }
}