package com.skillstorm.skillstorm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Question;
import com.skillstorm.skillstorm.repository.QuestionRepository;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question create(Question question) {
        return questionRepository.save(question);
    }

    public Question getById(int questionId) {
        return questionRepository.findById(questionId).orElse(null);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByQuizId(int quizId) {
        // Custom query would be better here
        return questionRepository.findAll().stream()
                .filter(q -> q.getQuiz().getQuizId() == quizId)
                .toList();
    }

    public Question updateQuestion(Question question) {
        return questionRepository.save(question);
    }

    public void delete(int questionId) {
        questionRepository.deleteById(questionId);
    }
}
