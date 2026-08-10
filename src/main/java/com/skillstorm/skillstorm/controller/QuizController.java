package com.skillstorm.skillstorm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.service.QuizService;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Quiz create(@RequestBody Quiz quiz) {
        return quizService.create(quiz);
    }

    @GetMapping("/{id}")
    public Quiz getById(@PathVariable Integer id) {
        return quizService.getById(id);
    }

    @GetMapping
    public List<Quiz> getAll() {
        return quizService.getAll();
    }

    @PutMapping("/{id}")
    public Quiz update(@PathVariable Integer id, @RequestBody Quiz updated) {
        return quizService.update(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        quizService.delete(id);
    }
}
