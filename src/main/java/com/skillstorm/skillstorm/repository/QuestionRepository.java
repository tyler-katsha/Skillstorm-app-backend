package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillstorm.skillstorm.model.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByQuizQuizId(int quizId);
}
