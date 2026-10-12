package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.skillstorm.model.Topic;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Optional<Topic> findByName(String name);
}
