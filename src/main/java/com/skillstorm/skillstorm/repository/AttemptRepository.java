package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.skillstorm.skillstorm.model.Attempt;

import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Integer> {
    List<Attempt> findByQuizQuizId(int quizId);
}
