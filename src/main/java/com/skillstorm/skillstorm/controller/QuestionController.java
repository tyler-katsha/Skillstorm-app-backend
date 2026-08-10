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

import com.skillstorm.skillstorm.model.Question;
import com.skillstorm.skillstorm.service.QuestionService;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Question create(@RequestBody Question q) {
        return questionService.create(q);
    }

    @GetMapping("/{id}")
    public Question getById(@PathVariable Integer id) {
        return questionService.getById(id);
    }

    @GetMapping
    public List<Question> getAll() {
        return questionService.getAll();
    }

    @PutMapping("/{id}")
    public Question update(@PathVariable Integer id, @RequestBody Question updated) {
        return questionService.update(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        questionService.delete(id);
    }
}
