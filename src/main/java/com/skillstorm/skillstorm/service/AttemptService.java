package com.skillstorm.skillstorm.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public AttemptService(AttemptRepository attemptRepository, QuizRepository quizRepository, UserRepository userRepository) {
        this.attemptRepository = attemptRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    public Attempt create(Attempt attempt) {
        return attemptRepository.save(attempt);
    }

    public Attempt create(int userId, int quizId, int score, LocalDateTime time) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> {
            return new NoSuchElementException(String.format("Could not find quiz by id %d.", quizId));
        });
        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new NoSuchElementException(String.format("Could not find user by id %d.", userId));
        });
        return attemptRepository.save(new Attempt(user, quiz));
    }

    public Attempt getById(int attemptId) {
        return attemptRepository.findById(attemptId).orElse(null);
    }

    public List<Attempt> getAll() {
        return attemptRepository.findAll();
    }

    public List<Attempt> getAttemptsByQuizId(int quizId) {
        // Custom query would be better here
        return attemptRepository.findAll().stream()
                .filter(a -> a.getQuiz().getQuizId() == quizId)
                .toList();
    }

    public Attempt update(Attempt attempt) {
        return attemptRepository.save(attempt);
    }

    public void delete(int attemptId) {
        attemptRepository.deleteById(attemptId);
    }
}
