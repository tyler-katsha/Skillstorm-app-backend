package com.skillstorm.skillstorm.service;

import java.util.List;

import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    @Cacheable(cacheNames = "question",key="#questionId")
    public Question getById(int questionId) {
        return questionRepository.findById(questionId).orElse(null);
    }
    @Cacheable(cacheNames = "question",key="'all'")
    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    @Cacheable(cacheNames = "quizQuestions",key="#quizId")
    public List<Question> getQuestionsByQuizId(int quizId) {
        return questionRepository.findByQuizQuizId(quizId);
    }
    @CachePut(cacheNames = "question",key="#questionId")
    @Caching(evict = {
            @CacheEvict(cacheNames = "question",key = "#questionId"),
            @CacheEvict(cacheNames = "question",key="'all'")
    })
    public Question update(int questionId,Question question) {

        Question existingQuestion = questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question doesn't exist"));

        existingQuestion.setAnswers(question.getAnswers());
        existingQuestion.setQuiz(question.getQuiz());
        existingQuestion.setText(question.getText());
        existingQuestion.setScore(question.getScore());
        existingQuestion.setAnswers(question.getAnswers());

        return questionRepository.save(existingQuestion);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "question",key = "#questionId"),
            @CacheEvict(cacheNames = "question",key="'all'")
    })
    public void delete(int questionId) {
        questionRepository.deleteById(questionId);
    }
}
