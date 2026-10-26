package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.Topic;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    List<Topic> toTopic(List<String> topicNames, Quiz quiz);
}
