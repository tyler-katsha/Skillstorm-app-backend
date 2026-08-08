package com.skillstorm.skillstorm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "leaderboard")
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leaderboard_id")
    private Integer leaderboardId;

    @Column(name = "rank", nullable = false)
    private int rank;

    @Column(name = "total_score", nullable = false)
    private int totalScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Integer getLeaderboardId() { return leaderboardId; }
    public void setLeaderboardId(Integer leaderboardId) { this.leaderboardId = leaderboardId; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
