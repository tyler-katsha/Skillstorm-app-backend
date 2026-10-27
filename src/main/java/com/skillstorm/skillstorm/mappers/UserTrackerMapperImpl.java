package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.QuizAttemptResponse;
import com.skillstorm.skillstorm.dto.UserTrackerRequest;
import com.skillstorm.skillstorm.dto.UserTrackerResponse;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.model.*;
import com.skillstorm.skillstorm.repository.QuizRepository;
import com.skillstorm.skillstorm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserTrackerMapperImpl implements UserTrackerMapper {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    @Override
    public UserTrackerResponse mapToDto(UserTracker tracker) {
        if (tracker == null) {
            throw new IllegalArgumentException("No Tracker found");
        }

        UserOverallStats stats = tracker.getOverallStats() != null
                ? tracker.getOverallStats()
                : new UserOverallStats();

        int currentLvl = stats.getCurrentLevel() != null ? stats.getCurrentLevel() : 1;
        int totalXp = stats.getTotalXp() != null ? stats.getTotalXp() : 0;
        int xpForNext = (currentLvl * 1000) - totalXp;

        List<QuizAttemptResponse> attempts = tracker.getAttemptRecords() != null
                ? tracker.getAttemptRecords().stream()
                  .map(this::mapAttemptToDto)
                  .toList()
                : List.of();

        return UserTrackerResponse.builder()
                .id(tracker.getId())
                .userId(tracker.getUser() != null ? tracker.getUser().getUserId() : null)
                .username(tracker.getUser() != null ? tracker.getUser().getUsername() : null)
                .currentLevel(currentLvl)
                .totalXp(totalXp)
                .xpToNewLevel(Math.max(0, xpForNext))
                .streak(stats.getCurrentStreak() != null ? stats.getCurrentStreak() : 0)
                .longestStreak(stats.getLongestStreak() != null ? stats.getLongestStreak() : 0)
                .lastActiveDate(stats.getLastActivityDate())
                .totalTimeSpentSeconds(stats.getTotalTimeSpentSeconds() != null ? stats.getTotalTimeSpentSeconds() : 0L)
                .quizzesTaken(stats.getTotalQuizzesCompleted() != null ? stats.getTotalQuizzesCompleted() : 0)
                .quizzesWon(stats.getQuizzesWon() != null ? stats.getQuizzesWon() : 0)
                .perfectQuizzes(stats.getPerfectQuizzes() != null ? stats.getPerfectQuizzes() : 0)
                .winRate(stats.getWinRate())
                .totalQuestionsAnswered(stats.getTotalQuestionsAnswered() != null ? stats.getTotalQuestionsAnswered() : 0)
                .totalCorrectAnswers(stats.getTotalCorrectAnswers() != null ? stats.getTotalCorrectAnswers() : 0)
                .accuracyRate(stats.getAccuracyRate())
                .completedQuizzes(tracker.getCompletedQuizzes() != null ? new HashSet<>(tracker.getCompletedQuizzes()) : new HashSet<>())
                .uncompletedQuizzes(tracker.getUncompletedQuizzes() != null ? new HashSet<>(tracker.getUncompletedQuizzes()) : new HashSet<>())
                .bookmarkedQuizzes(tracker.getBookmarkedQuizzes() != null ? new HashSet<>(tracker.getBookmarkedQuizzes()) : new HashSet<>())
                .attemptRecords(attempts)
                .build();
    }

    @Override
    public UserTracker mapBackToObj(UserTrackerRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("No request to map back to");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist with id: " + request.getUserId()));

        Set<Quiz> completed = request.getCompletedQuizId() != null
                ? new HashSet<>(quizRepository.findAllById(Set.of(request.getCompletedQuizId())))
                : new HashSet<>();

        Set<Quiz> bookmarked = (request.getBookmarkedQuizIds() != null && !request.getBookmarkedQuizIds().isEmpty())
                ? new HashSet<>(quizRepository.findAllById(request.getBookmarkedQuizIds()))
                : new HashSet<>();

        int gainedXp = request.getGainedXp() != null ? request.getGainedXp() : 0;
        int calculatedLevel = request.getCurrentLevel() != null
                ? request.getCurrentLevel()
                : 1 + (gainedXp / 1000);

        UserOverallStats stats = UserOverallStats.builder()
                .totalQuizzesCompleted(1)
                .quizzesWon(Boolean.TRUE.equals(request.getIsQuizWon()) ? 1 : 0)
                .perfectQuizzes(Boolean.TRUE.equals(request.getIsPerfectQuiz()) ? 1 : 0)
                .totalQuestionsAnswered(request.getQuestionsAnswered() != null ? request.getQuestionsAnswered() : 0)
                .totalCorrectAnswers(request.getCorrectAnswers() != null ? request.getCorrectAnswers() : 0)
                .totalTimeSpentSeconds(request.getTimeSpentSeconds() != null ? request.getTimeSpentSeconds() : 0L)
                .totalXp(gainedXp)
                .currentLevel(calculatedLevel)
                .currentStreak(1)
                .longestStreak(1)
                .lastActivityDate(LocalDate.now())
                .build();

        UserTracker tracker = UserTracker.builder()
                .user(user)
                .overallStats(stats)
                .completedQuizzes(completed)
                .uncompletedQuizzes(new HashSet<>())
                .bookmarkedQuizzes(bookmarked)
                .attemptRecords(new ArrayList<>())
                .build();

        // Map each Quiz ID into an UncompletedQuizProgress entity linked to this tracker
        if (request.getUncompletedQuizIds() != null && !request.getUncompletedQuizIds().isEmpty()) {
            Set<UncompletedQuizProgress> uncompleted = quizRepository.findAllById(request.getUncompletedQuizIds())
                    .stream()
                    .map(quiz -> UncompletedQuizProgress.builder()
                            .userTracker(tracker)
                            .quiz(quiz)
                            .currentQuestion(0)
                            .totalQuestions(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0)
                            .percentage(0)
                            .lastSavedAt(Instant.now())
                            .build())
                    .collect(Collectors.toSet());

            tracker.setUncompletedQuizzes(uncompleted);
        }

        if (request.getCompletedQuizId() != null && !completed.isEmpty()) {
            Quiz completedQuiz = completed.iterator().next();
            QuizAttemptRecord initialAttempt = QuizAttemptRecord.builder()
                    .userTracker(tracker)
                    .quiz(completedQuiz)
                    .attemptNumber(1)
                    .questionsAnswered(request.getQuestionsAnswered() != null ? request.getQuestionsAnswered() : 0)
                    .correctAnswers(request.getCorrectAnswers() != null ? request.getCorrectAnswers() : 0)
                    .timeSpentSeconds(request.getTimeSpentSeconds() != null ? request.getTimeSpentSeconds() : 0L)
                    .gainedXp(gainedXp)
                    .isQuizWon(request.getIsQuizWon())
                    .isPerfectQuiz(request.getIsPerfectQuiz())
                    .completedAt(Instant.now())
                    .build();

            tracker.addAttemptRecord(initialAttempt);
        }

        return tracker;
    }

    public QuizAttemptResponse mapAttemptToDto(QuizAttemptRecord record) {
        if (record == null) return null;

        return QuizAttemptResponse.builder()
                .attemptId(record.getAttemptId())
                .quizId(record.getQuiz() != null ? record.getQuiz().getQuizId() : null)
                .attemptNumber(record.getAttemptNumber())
                .questionsAnswered(record.getQuestionsAnswered())
                .correctAnswers(record.getCorrectAnswers())
                .timeSpentSeconds(record.getTimeSpentSeconds())
                .gainedXp(record.getGainedXp())
                .isQuizWon(record.getIsQuizWon())
                .isPerfectQuiz(record.getIsPerfectQuiz())
                .completedAt(record.getCompletedAt())
                .build();
    }
}