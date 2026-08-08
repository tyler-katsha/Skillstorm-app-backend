package com.skillstorm.skillstorm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Question;
import com.skillstorm.skillstorm.repository.QuestionRepository;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question create(Question q) {
        return questionRepository.save(q);
    }

    public Question getById(Integer id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));
    }

    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    public Question update(Integer id, Question updated) {
        Question existing = getById(id);

        existing.setQuestionTitle(updated.getQuestionTitle());
        existing.setAnswer(updated.getAnswer());
        existing.setPossibleAnswers(updated.getPossibleAnswers());
        existing.setScore(updated.getScore());

        return questionRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!questionRepository.existsById(id)) {
            throw new IllegalArgumentException("Question not found: " + id);
        }
        questionRepository.deleteById(id);
    }
}
