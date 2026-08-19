package com.skillstorm.skillstorm.service;

import java.util.List;

import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.skillstorm.skillstorm.model.Leaderboard;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.LeaderboardRepository;
import com.skillstorm.skillstorm.repository.UserRepository;

@Service
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;

    public LeaderboardService(LeaderboardRepository leaderboardRepository, UserRepository userRepository) {
        this.leaderboardRepository = leaderboardRepository;
        this.userRepository = userRepository;
    }
    /* We don't create this method more than once for the entire lifecycle of the application.
     We will only update the existing 10 unless we want to keep track and update the entire
     ranking system and keep track of the ranking's for all user's */
    public Leaderboard create(int rank, int totalScore, Integer userId) {

        User user = userId == null ? null : userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Leaderboard entry = new Leaderboard();
        entry.setRank(rank);
        entry.setTotalScore(totalScore);
        entry.setUser(user);

        return leaderboardRepository.save(entry);
    }

    @Cacheable(cacheNames = "leaderboard",key="#id")
    public Leaderboard getById(Integer id) {
        return leaderboardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leaderboard entry not found: " + id));
    }

    @Cacheable(cacheNames = "leaderboard",key="'all'")
    public List<Leaderboard> getAll() {
        return leaderboardRepository.findAll();
    }

    @CachePut(cacheNames = "leaderboard",key="#id")
    @Caching(evict = {
            @CacheEvict(cacheNames = "leaderboard",key = "#id"),
            @CacheEvict(cacheNames = "leaderboard",key="'all'")
    })
    public Leaderboard update(Integer id, Leaderboard updated) {
        Leaderboard existing = getById(id);

        existing.setRank(updated.getRank());
        existing.setTotalScore(updated.getTotalScore());
        existing.setUser(updated.getUser());

        return leaderboardRepository.save(existing);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "leaderboard",key = "#id"),
            @CacheEvict(cacheNames = "leaderboard",key="'all'")
    })
    public void delete(Integer id) {
        if (!leaderboardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leaderboard entry not found: " + id);
        }

        leaderboardRepository.deleteById(id);
    }
}
