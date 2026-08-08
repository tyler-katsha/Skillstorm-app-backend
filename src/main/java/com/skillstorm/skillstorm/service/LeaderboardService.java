package com.skillstorm.skillstorm.service;

import java.util.List;

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

    public Leaderboard create(int rank, int totalScore, Integer userId) {
        User user = userId == null ? null : userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Leaderboard entry = new Leaderboard();
        entry.setRank(rank);
        entry.setTotalScore(totalScore);
        entry.setUser(user);

        return leaderboardRepository.save(entry);
    }

    public Leaderboard getById(Integer id) {
        return leaderboardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leaderboard entry not found: " + id));
    }

    public List<Leaderboard> getAll() {
        return leaderboardRepository.findAll();
    }

    public Leaderboard update(Integer id, Leaderboard updated) {
        Leaderboard existing = getById(id);

        existing.setRank(updated.getRank());
        existing.setTotalScore(updated.getTotalScore());
        existing.setUser(updated.getUser());

        return leaderboardRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!leaderboardRepository.existsById(id)) {
            throw new IllegalArgumentException("Leaderboard entry not found: " + id);
        }
        leaderboardRepository.deleteById(id);
    }
}
