package com.skillstorm.skillstorm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_trackers")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"user", "attemptRecords", "uncompletedQuizzes", "completedQuizzes", "bookmarkedQuizzes"})
@ToString(exclude = {"user", "attemptRecords", "uncompletedQuizzes", "completedQuizzes", "bookmarkedQuizzes"})
public class UserTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Embedded
    @Builder.Default
    private UserOverallStats overallStats = new UserOverallStats();

    @OneToMany(mappedBy = "userTracker", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuizAttemptRecord> attemptRecords = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_tracker_uncompleted_quizzes",
            joinColumns = @JoinColumn(name = "user_tracker_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @Builder.Default
    private List<Quiz> uncompletedQuizzes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_tracker_completed_quizzes",
            joinColumns = @JoinColumn(name = "user_tracker_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @Builder.Default
    private List<Quiz> completedQuizzes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_tracker_bookmarked_quizzes",
            joinColumns = @JoinColumn(name = "user_tracker_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @Builder.Default
    private List<Quiz> bookmarkedQuizzes = new ArrayList<>();

    public void addAttemptRecord(QuizAttemptRecord record) {
        attemptRecords.add(record);
        record.setUserTracker(this);
    }

    public int getNextAttemptNumberForQuiz(Integer quizId) {
        return (int) attemptRecords.stream()
                .filter(a -> a.getQuiz() != null && a.getQuiz().getQuizId().equals(quizId))
                .count() + 1;
    }
}