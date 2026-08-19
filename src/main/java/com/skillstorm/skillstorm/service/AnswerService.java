package com.skillstorm.skillstorm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Answer;
import com.skillstorm.skillstorm.repository.AnswerRepository;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public Answer createAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    @Cacheable(cacheNames = "answer",key="#answerId")
    public Answer findById(int answerId) {
        return answerRepository.findById(answerId).orElse(null);
    }

    @Cacheable(cacheNames = "answer",key="'all'")
    public List<Answer> findAll() {
        return answerRepository.findAll();
    }

    @Cacheable(cacheNames = "questionAnswers",key="#questionId")
    public List<Answer> getAnswersByQuestionId(int questionId) {
        return answerRepository.findByQuestionQuestionId(questionId);
    }

    @CachePut(cacheNames = "answer",key = "#answer.answerId")
    @Caching(evict = {
            @CacheEvict(cacheNames = "answer", key = "#answer.answerId"),
            @CacheEvict(cacheNames = "answer", key = "'all'"),
            @CacheEvict(cacheNames = "answer", key = "#answer.question.questionId")
    })
    public Answer updateAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "answer", key = "#answerId"),
            @CacheEvict(cacheNames = "answer", key = "'all'"),
    })
    public void deleteAnswer(int answerId) {
        answerRepository.deleteById(answerId);
    }
}
