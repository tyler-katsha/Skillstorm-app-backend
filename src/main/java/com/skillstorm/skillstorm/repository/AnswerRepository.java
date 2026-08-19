package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.skillstorm.skillstorm.model.Answer;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    List<Answer> findByQuestionQuestionId(int questionId);
}
