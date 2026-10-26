package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.QuizAttemptResponse;
import com.skillstorm.skillstorm.dto.QuizSummaryDto;
import com.skillstorm.skillstorm.dto.UserTrackerRequest;
import com.skillstorm.skillstorm.dto.UserTrackerResponse;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.model.*;
import com.skillstorm.skillstorm.repository.QuizRepository;
import com.skillstorm.skillstorm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserTrackerMapperImpl implements UserTrackerMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

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
                .streak(stats.getCurrentStreak())
                .longestStreak(stats.getLongestStreak())
                .lastActiveDate(stats.getLastActivityDate())
                .totalTimeSpentSeconds(stats.getTotalTimeSpentSeconds())
                .quizzesTaken(stats.getTotalQuizzesCompleted())
                .quizzesWon(stats.getQuizzesWon())
                .perfectQuizzes(stats.getPerfectQuizzes())
                .winRate(stats.getWinRate())
                .totalQuestionsAnswered(stats.getTotalQuestionsAnswered())
                .totalCorrectAnswers(stats.getTotalCorrectAnswers())
                .accuracyRate(stats.getAccuracyRate())
                .completedQuizzes(tracker.getCompletedQuizzes())
                .uncompletedQuizzes(tracker.getUncompletedQuizzes())
                .bookmarkedQuizzes(tracker.getBookmarkedQuizzes())
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

        List<Quiz> completed = request.getCompletedQuizId() != null
                ? new ArrayList<>(quizRepository.findAllById(Set.of(request.getCompletedQuizId())))
                : new ArrayList<>();

        List<Quiz> uncompleted = (request.getUncompletedQuizIds() != null && !request.getUncompletedQuizIds().isEmpty())
                ? new ArrayList<>(quizRepository.findAllById(request.getUncompletedQuizIds()))
                : new ArrayList<>();

        List<Quiz> bookmarked = (request.getBookmarkedQuizIds() != null && !request.getBookmarkedQuizIds().isEmpty())
                ? new ArrayList<>(quizRepository.findAllById(request.getBookmarkedQuizIds()))
                : new ArrayList<>();

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
                .uncompletedQuizzes(uncompleted)
                .bookmarkedQuizzes(bookmarked)
                .attemptRecords(new ArrayList<>())
                .build();

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