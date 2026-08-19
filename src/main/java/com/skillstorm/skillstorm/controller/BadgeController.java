package com.skillstorm.skillstorm.controller;

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

import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.service.BadgeService;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeService badgeService;
    private final InspectionCacheService inspectionCacheService;

    public BadgeController(BadgeService badgeService,InspectionCacheService inspectionCacheService) {
        this.badgeService = badgeService;
        this.inspectionCacheService = inspectionCacheService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Badge create(@RequestParam String title,  @RequestParam String description, @RequestParam Integer userId) {
        return badgeService.create(title, description, userId);
    }

    @GetMapping("/{id}")
    public Badge getById(@PathVariable Integer id) {
        return badgeService.getById(id);
    }

    @GetMapping
    public List<Badge> getAll() {
        return badgeService.getAll();
    }

    @PutMapping("/{id}")
    public Badge update(@PathVariable Integer id, @RequestBody Badge updated) {
        return badgeService.update(id, updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        badgeService.delete(id);
    }

    @GetMapping("/cache")
    public ResponseEntity<String> cache(){
        inspectionCacheService.inspectCache("badge");
        return ResponseEntity.ok("Cache was checked");
    }
}
