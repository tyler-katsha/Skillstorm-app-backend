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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.service.BadgeService;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Badge create(@RequestParam String title,
                         @RequestParam(required = false) String description,
                         @RequestParam(required = false) Integer userId) {
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

    @PutMapping("/")
    public Badge update(@RequestBody Badge updated) {
        int id = updated.getAchievementId();
        return badgeService.update(id, updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        badgeService.delete(id);
    }
}
