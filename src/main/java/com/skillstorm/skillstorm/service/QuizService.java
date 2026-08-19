package com.skillstorm.skillstorm.service;

import java.util.List;

import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.mappers.QuizMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.dto.QuizDTO;
import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.repository.QuizRepository;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    @Autowired
    public QuizService (QuizRepository quizRepository, QuizMapper quizMapper) {
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
    }

    public Quiz create(Quiz quiz) {
        return quizRepository.save(quiz);
    }
    @Cacheable(cacheNames = "quiz",key="#quizId")
    public QuizDTO getById(int quizId) {

        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz doesn't exist"));

        return quizMapper.mapToDto(existingQuiz);
    }
    @Cacheable(cacheNames = "quiz",key="'all'")
    public List<QuizDTO> getAll() {

        return quizRepository.findAll()
            .stream()
            .map(quizMapper::mapToDto)
            .toList();
    }
    @CachePut(cacheNames = "quiz",key="#quiz.quizId")
    @Caching(evict = {
            @CacheEvict(cacheNames = "quiz",key = "#quiz.quizId"),
            @CacheEvict(cacheNames = "quiz",key="'all'")
    })
    public Quiz update(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "quiz",key = "#quizId"),
            @CacheEvict(cacheNames = "quiz",key="'all'")
    })
    public void delete(int quizId) {
        quizRepository.deleteById(quizId);
    }
}
