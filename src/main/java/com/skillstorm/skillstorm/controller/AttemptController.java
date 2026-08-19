package com.skillstorm.skillstorm.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.skillstorm.skillstorm.service.InspectionCacheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.skillstorm.model.Attempt;
import com.skillstorm.skillstorm.service.AttemptService;

@RestController
@RequestMapping("/api/attempts")
public class AttemptController {

    private final AttemptService attemptService;
    private final InspectionCacheService inspectionCacheService;

    public AttemptController(AttemptService attemptService,InspectionCacheService inspectionCacheService) {
        this.attemptService = attemptService;
        this.inspectionCacheService = inspectionCacheService;
    }

    // Simple endpoint using explicit relationship ids
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Attempt create(@RequestParam  Integer userId,
                            @RequestParam Integer quizId,
                            @RequestParam int score,
                            @RequestParam LocalDateTime time) {
        return attemptService.create(userId, quizId, score, time);
    }

    @GetMapping("/{id}")
    public Attempt getById(@PathVariable Integer id) {
        return attemptService.getById(id);
    }

    @GetMapping
    public List<Attempt> getAll() {
        return attemptService.getAll();
    }

    @PutMapping("/{id}")
    public Attempt update(@PathVariable Integer id, @RequestBody Attempt updated) {
        // TODO: Decide whether it is acceptable to force clients to send the ID in both the path and request body.
        return attemptService.update(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        attemptService.delete(id);
    }

    @GetMapping("/cache")
    public ResponseEntity<String> cache(){
        inspectionCacheService.inspectCache("attempt");
        return ResponseEntity.ok("Cache was checked");
    }
}
