package com.skillstorm.skillstorm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Topic;
import com.skillstorm.skillstorm.repository.TopicRepository;

@Service
public class TopicService {
    private final TopicRepository topicRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public Topic create(Topic topic) {
        return topicRepository.save(topic);
    }

    public Topic getById(int topicId) {
        return topicRepository.findById(topicId).orElse(null);
    }

    public Topic getTopicByName(String name) {
        return topicRepository.findByName(name);
    }

    public List<Topic> getAll() {
        return topicRepository.findAll();
    }

    public Topic update(Topic topic) {
        return topicRepository.save(topic);
    }

    public void delete(int topicId) {
        topicRepository.deleteById(topicId);
    }
}
