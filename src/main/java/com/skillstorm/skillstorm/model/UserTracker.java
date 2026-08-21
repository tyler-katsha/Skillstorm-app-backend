package com.skillstorm.skillstorm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_tracker")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;

    private Integer quizzesTaken;
    private Integer quizzesWon;
    private Integer streak;
}
