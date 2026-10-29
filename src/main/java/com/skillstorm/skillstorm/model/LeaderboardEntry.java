package com.skillstorm.skillstorm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leaderboard")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {

    @Id
    @Column(name = "leaderboard_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "`rank`", nullable = false)
    private int rank;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "xp", nullable = false)
    private int xp;

    @Column(name = "level", nullable = false)
    private int level;
}
