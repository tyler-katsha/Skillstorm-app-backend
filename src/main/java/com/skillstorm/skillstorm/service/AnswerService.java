package com.skillstorm.skillstorm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Answer;
import com.skillstorm.skillstorm.repository.AnswerRepository;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public Answer create(Answer answer) {
        return answerRepository.save(answer);
    }

    public Answer getById(int answerId) {
        return answerRepository.findById(answerId).orElse(null);
    }

    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    public List<Answer> getAnswersByQuestionId(int questionId) {
        // Custom query would be better here
        return answerRepository.findAll().stream()
                .filter(a -> a.getQuestion().getQuestionId() == questionId)
                .toList();
    }

    public Answer updateAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    public void delete(int answerId) {
        answerRepository.deleteById(answerId);
    }
}
