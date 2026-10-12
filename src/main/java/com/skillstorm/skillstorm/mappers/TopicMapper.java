package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.Topic;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")

public interface TopicMapper {
    Set<Topic> toTopic(Set<String> topicNames, Quiz quiz);
}
