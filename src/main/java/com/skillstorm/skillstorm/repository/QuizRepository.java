package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.skillstorm.skillstorm.model.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {}
