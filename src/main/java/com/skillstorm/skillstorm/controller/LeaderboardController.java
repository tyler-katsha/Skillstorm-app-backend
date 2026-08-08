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

import com.skillstorm.skillstorm.model.Leaderboard;
import com.skillstorm.skillstorm.service.LeaderboardService;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Leaderboard create(@RequestParam int rank,
                               @RequestParam int totalScore,
                               @RequestParam(required = false) Integer userId) {
        return leaderboardService.create(rank, totalScore, userId);
    }

    @GetMapping("/{id}")
    public Leaderboard getById(@PathVariable Integer id) {
        return leaderboardService.getById(id);
    }

    @GetMapping
    public List<Leaderboard> getAll() {
        return leaderboardService.getAll();
    }

    @PutMapping("/{id}")
    public Leaderboard update(@PathVariable Integer id, @RequestBody Leaderboard updated) {
        return leaderboardService.update(id, updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        leaderboardService.delete(id);
    }
}
