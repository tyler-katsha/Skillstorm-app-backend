package com.skillstorm.skillstorm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Attempt;
import com.skillstorm.skillstorm.repository.AttemptRepository;

@Service
public class AttemptService {
    private final AttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    @Autowired
    public AttemptService(AttemptRepository attemptRepository, UserRepository userRepository, QuizRepository quizRepository) {
        this.AttemptRepository = attemptRepository;
        this.UserRepository = userRepository;
        this.QuizRepository = quizRepository;
    }

    public Attempt create(Attempt attempt) {
        return attemptRepository.save(attempt);
    }

    public Attempt create(int userId, int quizId, int score, LocalDateTime time) {
        User user = userRepository.findById(userId);
        Quiz quiz = quizRepository.findById(quizId);
        return attemptRepository.save(new Attempt(user, quiz));
    }

    public Attempt getById(int attemptId) {
        return attemptRepository.findById(attemptId).orElse(null);
    }

    public List<Attempt> getAllAttempts() {
        return attemptRepository.findAll();
    }

    public List<Attempt> getAttemptsByQuizId(int quizId) {
        // Custom query would be better here
        return attemptRepository.findAll().stream()
                .filter(a -> a.getQuiz().getQuizId() == quizId)
                .toList();
    }

    public Attempt updateAttempt(Attempt attempt) {
        return attemptRepository.save(attempt);
    }

    public void delete(int attemptId) {
        attemptRepository.deleteById(attemptId);
    }
}
