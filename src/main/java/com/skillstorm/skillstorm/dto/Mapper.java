package com.skillstorm.skillstorm.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.skillstorm.skillstorm.model.Answer;
import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.model.Question;
import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.Topic;
import com.skillstorm.skillstorm.model.User;

@Component
public class Mapper {
    public QuizDTO mapToDto(Quiz quiz) {
        if (quiz == null) {
            return null;
        }
        String text = quiz.getTitle();
        String difficulty = quiz.getDifficulty();
        List<String> topics = quiz.getTopics()
            .stream()
            .map(Topic::getName)
            .toList();
        List<QuestionDTO> questions = quiz.getQuestions()
            .stream()
            .map((Question question) -> {
                return new QuestionDTO(
                    question.getText(),
                    question.getScore(),
                    question.getAnswers()
                        .stream()
                        .map((Answer answer) -> {
                            return new AnswerDTO(answer.getAnswerText(), answer.isCorrect());
                        })
                        .toList()
                );
            })
            .toList();
        return new QuizDTO(
            text,
            difficulty, 
            topics,
            questions
        );
    }

    public UserDTO mapToDto(User user) {
        if (user == null) {
            return null;
        }
        List<BadgeDTO> badges = user
            .getBadges()
            .stream()
            .map((Badge badge) -> {
                return new BadgeDTO(badge.getName(), badge.getDescription());
            })
            .toList();
        return new UserDTO(user.getUsername(), user.getXp(), badges);
    }
}
