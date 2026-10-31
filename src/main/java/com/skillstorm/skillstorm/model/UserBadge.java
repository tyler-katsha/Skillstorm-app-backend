package com.skillstorm.skillstorm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_badge", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","badge_id"}))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"user","badge"})
@ToString(exclude = {"user","badge"})
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_badge_id")
    private Integer id;

    @Builder.Default
    @Column(name = "current_progress",nullable = false)
    private Integer currentProgress = 0;

    @Builder.Default
    @Column(name = "is_earned",nullable = false)
    private boolean isEarned = false;

    @Column(name = "earned_at")
    private Instant earnedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id",nullable = false)
    private Badge badge;
}
