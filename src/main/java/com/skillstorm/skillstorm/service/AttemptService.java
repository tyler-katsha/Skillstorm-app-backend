package com.skillstorm.skillstorm.service;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Attempt;
import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.AttemptRepository;
import com.skillstorm.skillstorm.repository.QuizRepository;
import com.skillstorm.skillstorm.repository.UserRepository;

@Service
public class AttemptService {
    private final AttemptRepository attemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public AttemptService(AttemptRepository attemptRepository, QuizRepository quizRepository, UserRepository userRepository) {
        this.attemptRepository = attemptRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    public Attempt create(Attempt attempt) {
        return attemptRepository.save(attempt);
    }

    public Attempt create(int userId, int quizId, int score, LocalDateTime time) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        Attempt savedAttempted = Attempt.builder()
                .quiz(quiz)
                .user(user)
                .score(score)
                .time(time)
                .build();

        return attemptRepository.save(savedAttempted);
    }
    @Cacheable(cacheNames = "attempt",key="#attemptId")
    public Attempt getById(int attemptId) {
        return attemptRepository.findById(attemptId).orElse(null);
    }

    @Cacheable(cacheNames = "attempt",key="'all'")
    public List<Attempt> getAll() {
        return attemptRepository.findAll();
    }

    @Cacheable(cacheNames = "quizAttempts",key="#quizId")
    public List<Attempt> getAttemptsByQuizId(int quizId) {
        return attemptRepository.findByQuizQuizId(quizId);
    }

    @CachePut(cacheNames = "attempt",key = "#attempt.attemptId")
    @Caching(evict = {
            @CacheEvict(cacheNames = "attempt",key="#attempt.attemptId"),
            @CacheEvict(cacheNames = "attempt",key="#attempt.quiz.quizId"),
            @CacheEvict(cacheNames = "attempt",key = "'all'")
    })
    public Attempt update(Attempt attempt) {
        return attemptRepository.save(attempt);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "attempt",key="#attemptId"),
            @CacheEvict(cacheNames = "attempt",key = "'all'")
    })
    public void delete(int attemptId) {
        attemptRepository.deleteById(attemptId);
    }
}
