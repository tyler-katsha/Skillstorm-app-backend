package com.skillstorm.skillstorm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.dto.QuizDTO;
import com.skillstorm.skillstorm.dto.Mapper;
import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.repository.QuizRepository;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final Mapper mapper;

    @Autowired
    public QuizService (QuizRepository quizRepository, Mapper mapper) {
        this.quizRepository = quizRepository;
        this.mapper = mapper;
    }

    public Quiz create(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public QuizDTO getById(int quizId) {
        return mapper.mapToDto(quizRepository.findById(quizId).orElseThrow());
    }

    public List<QuizDTO> getAll() {
        List<Quiz> allQuizzes = quizRepository.findAll();
        return allQuizzes
            .stream()
            .map(mapper::mapToDto)
            .toList();
    }

    public Quiz update(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public void delete(int quizId) {
        quizRepository.deleteById(quizId);
    }
}
