package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.Topic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TopicMapperImpl implements TopicMapper{
    @Override
    public List<Topic> toTopic(List<String> topicNames, Quiz quiz) {
        if(topicNames == null) return null;

        return topicNames.stream()
                .map(s -> {

                    return Topic.builder()
                            .name(s)
                            .quizzes(List.of(quiz))
                            .build();
                })
                .toList();
    }
}
