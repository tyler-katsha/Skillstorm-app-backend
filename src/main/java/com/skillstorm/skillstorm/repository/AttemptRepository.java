package com.skillstorm.skillstorm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.skillstorm.skillstorm.model.Attempt;

public interface AttemptRepository extends JpaRepository<Attempt, Integer> {}
