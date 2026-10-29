package com.skillstorm.skillstorm.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user_trackers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"user", "attemptRecords", "uncompletedQuizzes", "completedQuizzes", "bookmarkedQuizzes"})
@ToString(exclude = {"user", "attemptRecords", "uncompletedQuizzes", "completedQuizzes", "bookmarkedQuizzes"})
public class UserTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tracker_id")
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

    @OneToMany(mappedBy = "userTracker", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UncompletedQuizProgress> uncompletedQuizzes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_tracker_completed_quizzes",
            joinColumns = @JoinColumn(name = "user_tracker_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @Builder.Default
    private Set<Quiz> completedQuizzes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_tracker_bookmarked_quizzes",
            joinColumns = @JoinColumn(name = "user_tracker_id"),
            inverseJoinColumns = @JoinColumn(name = "quiz_id")
    )
    @Builder.Default
    private Set<Quiz> bookmarkedQuizzes = new HashSet<>();

    public void addAttemptRecord(QuizAttemptRecord record) {
        if (this.attemptRecords == null) {
            this.attemptRecords = new ArrayList<>();
        }
        this.attemptRecords.add(record);
        record.setUserTracker(this);
    }

    public void addOrUpdateUncompletedProgress(Quiz quiz, int currentQuestion, int totalQuestions, int percentage) {
        if (this.uncompletedQuizzes == null) {
            this.uncompletedQuizzes = new HashSet<>();
        }

        this.uncompletedQuizzes.stream()
                .filter(p -> p.getQuiz() != null && Objects.equals(p.getQuiz().getQuizId(), quiz.getQuizId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> {
                            existing.setCurrentQuestion(currentQuestion);
                            existing.setTotalQuestions(totalQuestions);
                            existing.setPercentage(percentage);
                            existing.setLastSavedAt(java.time.Instant.now());
                        },
                        () -> {
                            UncompletedQuizProgress newProgress = UncompletedQuizProgress.builder()
                                    .userTracker(this)
                                    .quiz(quiz)
                                    .currentQuestion(currentQuestion)
                                    .totalQuestions(totalQuestions)
                                    .percentage(percentage)
                                    .lastSavedAt(java.time.Instant.now())
                                    .build();
                            this.uncompletedQuizzes.add(newProgress);
                        }
                );
    }

    public void removeUncompletedProgress(Integer quizId) {
        if (this.uncompletedQuizzes != null && quizId != null) {
            this.uncompletedQuizzes.removeIf(p -> p.getQuiz() != null && Objects.equals(p.getQuiz().getQuizId(), quizId));
        }
    }

    public int getNextAttemptNumberForQuiz(Integer quizId) {
        if (this.attemptRecords == null || quizId == null) {
            return 1;
        }
        return (int) attemptRecords.stream()
                .filter(a -> a.getQuiz() != null && Objects.equals(a.getQuiz().getQuizId(), quizId))
                .count() + 1;
    }
}