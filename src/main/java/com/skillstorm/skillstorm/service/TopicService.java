package com.skillstorm.skillstorm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Topic;
import com.skillstorm.skillstorm.repository.TopicRepository;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public Topic create(Topic topic) {
        return topicRepository.save(topic);
    }

    public Topic getById(Integer id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + id));
    }

    public List<Topic> getAll() {
        return topicRepository.findAll();
    }

    public Topic update(Integer id, Topic updated) {
        Topic existing = getById(id);

        existing.setTitle(updated.getTitle());

        return topicRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!topicRepository.existsById(id)) {
            throw new IllegalArgumentException("Topic not found: " + id);
        }
        topicRepository.deleteById(id);
    }
}
