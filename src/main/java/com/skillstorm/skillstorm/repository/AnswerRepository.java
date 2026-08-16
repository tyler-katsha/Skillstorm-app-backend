package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.skillstorm.skillstorm.model.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {}
