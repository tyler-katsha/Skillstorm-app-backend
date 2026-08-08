package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.skillstorm.model.Topic;

public interface TopicRepository extends JpaRepository<Topic, Integer> {}
