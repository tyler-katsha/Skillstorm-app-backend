package com.skillstorm.skillstorm.controller;

import java.util.List;

import com.skillstorm.skillstorm.service.InspectionCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final InspectionCacheService inspectionCacheService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Question create(@RequestBody Question q) {
        return questionService.create(q);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public Question getById(@PathVariable Integer id) {
        return questionService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','EMPLOYEE','ADMIN')")
    public List<Question> getAll() {
        return questionService.getAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Question update(@PathVariable Integer id, @RequestBody Question updated) {
        return questionService.update(id, updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Integer id) {
        questionService.delete(id);
    }

    @GetMapping("/cache")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cache(){
        inspectionCacheService.inspectCache("question");
        return ResponseEntity.ok("Cache was checked");
    }
}
