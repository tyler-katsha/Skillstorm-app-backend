package com.skillstorm.skillstorm.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Attempt;
import com.skillstorm.skillstorm.model.Question;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.AttemptRepository;
import com.skillstorm.skillstorm.repository.QuestionRepository;
import com.skillstorm.skillstorm.repository.UserRepository;

@Service
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    public AttemptService(AttemptRepository attemptRepository,
                           UserRepository userRepository,
                           QuestionRepository questionRepository) {
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    public Attempt create(Integer userId, Integer questionId, int score, LocalDateTime time) {
        User user = userId == null ? null : userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Question question = questionId == null ? null : questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        Attempt attempt = new Attempt();
        attempt.setUser(user);
        attempt.setQuestion(question);
        attempt.setScore(score);
        attempt.setTime(time != null ? time : LocalDateTime.now());

        return attemptRepository.save(attempt);
    }

    public Attempt getById(Integer id) {
        return attemptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found: " + id));
    }

    public List<Attempt> getAll() {
        return attemptRepository.findAll();
    }

    public Attempt update(Integer id, Attempt updated) {
        Attempt existing = getById(id);

        existing.setScore(updated.getScore());
        existing.setTime(updated.getTime());

        existing.setUser(updated.getUser());
        existing.setQuestion(updated.getQuestion());

        return attemptRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!attemptRepository.existsById(id)) {
            throw new IllegalArgumentException("Attempt not found: " + id);
        }
        attemptRepository.deleteById(id);
    }
}
