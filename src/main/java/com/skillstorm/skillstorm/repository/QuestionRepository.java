package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.skillstorm.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {}
