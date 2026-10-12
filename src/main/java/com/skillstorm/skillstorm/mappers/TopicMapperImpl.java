package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.Topic;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TopicMapperImpl implements TopicMapper{
    @Override
    public Set<Topic> toTopic(Set<String> topicNames, Quiz quiz) {
        if(topicNames == null) return null;

        return topicNames.stream()
                .map(s -> {

                    return Topic.builder()
                            .name(s)
                            .quizzes(Set.of(quiz))
                            .build();
                })
                .collect(Collectors.toSet());
    }
}
