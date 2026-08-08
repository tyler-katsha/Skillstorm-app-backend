package com.skillstorm.skillstorm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.repository.QuizRepository;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public Quiz create(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public Quiz getById(Integer id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + id));
    }

    public List<Quiz> getAll() {
        return quizRepository.findAll();
    }

    public Quiz update(Integer id, Quiz updated) {
        Quiz existing = getById(id);

        existing.setTitle(updated.getTitle());
        existing.setDifficulty(updated.getDifficulty());
        existing.setQuestions(updated.getQuestions());
        existing.setTopics(updated.getTopics());
        existing.setTotalScore(updated.getTotalScore());

        return quizRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!quizRepository.existsById(id)) {
            throw new IllegalArgumentException("Quiz not found: " + id);
        }
        quizRepository.deleteById(id);
    }
}
