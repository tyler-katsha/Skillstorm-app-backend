package com.skillstorm.skillstorm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    @Cacheable(cacheNames = "topic",key="#topicId")
    public Topic getById(int topicId) {
        return topicRepository.findById(topicId).orElse(null);
    }

    @Cacheable(cacheNames = "topicNames",key="#name")
    public Topic getTopicByName(String name) {
        return topicRepository.findByName(name);
    }

    @Cacheable(cacheNames = "topic",key="'all'")
    public List<Topic> getAll() {
        return topicRepository.findAll();
    }

    @CachePut(cacheNames = "topic",key="#topic.topicId")
    @Caching(evict = {
            @CacheEvict(cacheNames = "topic",key="#topic.topicId"),
            @CacheEvict(cacheNames = "topic",key="'all'"),
            @CacheEvict(cacheNames = "topicNames",key="#topic.name")
    })
    public Topic update(Topic topic) {
        return topicRepository.save(topic);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "topic",key="#topicId"),
            @CacheEvict(cacheNames = "topic",key="'all'")
    })
    public void delete(int topicId) {
        topicRepository.deleteById(topicId);
    }
}
