package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.QuestionDTO;
import com.skillstorm.skillstorm.dto.QuizDTO;
import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuizMapperImpl implements QuizMapper{
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public QuizDTO mapToDto(Quiz quiz) {

        if(quiz == null){
            throw new IllegalArgumentException("Unable to process Quiz Object");
        }

        String title = quiz.getTitle();
        String difficulty = quiz.getDifficulty();

        List<String> topicNames = getTopicsByName(quiz.getTopics());

        List<QuestionDTO> dtos = questionMapper.mapToDto(quiz.getQuestions());

        return new QuizDTO(title,difficulty,topicNames,dtos);
    }

    private List<String> getTopicsByName(List<Topic> topics){
        return topics.stream().map(Topic::getName).toList();
    }

}
