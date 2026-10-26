package com.skillstorm.skillstorm.service;

import com.skillstorm.skillstorm.dto.*;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.mappers.UserTrackerMapper;
import com.skillstorm.skillstorm.model.*;
import com.skillstorm.skillstorm.repository.QuizRepository;
import com.skillstorm.skillstorm.repository.UserRepository;
import com.skillstorm.skillstorm.repository.UserTrackerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTrackerService {

    private final UserRepository userRepository;
    private final UserTrackerRepository userTrackerRepository;
    private final QuizRepository quizRepository;
    private final PointService pointService;
    private final UserTrackerMapper trackerMapper;

    @Transactional
    public SubmissionResultDto submit(int id, SubmissionPayload payload, int userId) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz doesn't exist with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist with id: " + userId));

        payload.setQuizId(id);
        payload.setUserId(userId);

        // 1. Grade the submission
        List<UserAnswerSubmission> submittedAnswers = payload.getAnswers() != null
                ? payload.getAnswers()
                : Collections.emptyList();

        int totalQuestions = (quiz.getQuestions() != null) ? quiz.getQuestions().size() : submittedAnswers.size();
        int questionsAnswered = submittedAnswers.size();
        int correctAnswers = 0;

        Map<Integer, Integer> answerMap = submittedAnswers.stream()
                .collect(Collectors.toMap(UserAnswerSubmission::getQuestionId, UserAnswerSubmission::getSelectedAnswerId, (existing, replacement) -> replacement));

        if (quiz.getQuestions() != null) {
            for (Question question : quiz.getQuestions()) {
                Integer selectedId = answerMap.get(question.getQuestionId());
                if (selectedId != null && question.getAnswers() != null) {
                    boolean isCorrect = question.getAnswers().stream()
                            .anyMatch(a -> a.getAnswerId().equals(selectedId) && Boolean.TRUE.equals(a.isCorrect()));
                    if (isCorrect) {
                        correctAnswers++;
                    }
                }
            }
        }

        boolean isPerfectQuiz = totalQuestions > 0 && correctAnswers == totalQuestions;
        boolean isQuizWon = totalQuestions > 0 && ((double) correctAnswers / totalQuestions) >= 0.70;

        int gainedXp = (correctAnswers * 10) + (isQuizWon ? 50 : 0) + (isPerfectQuiz ? 50 : 0);
        long timeSpentSeconds = payload.getTimeSpentSeconds();

        UserTracker tracker = userTrackerRepository.findByUserId(user.getUserId())
                .orElseGet(() -> UserTracker.builder()
                        .user(user)
                        .overallStats(new UserOverallStats())
                        .build());

        UserOverallStats stats = tracker.getOverallStats();
        if (stats == null) {
            stats = new UserOverallStats();
            tracker.setOverallStats(stats);
        }

        int previousLevel = stats.getCurrentLevel() != null ? stats.getCurrentLevel() : 1;

        tracker.getCompletedQuizzes().add(quiz);
        tracker.getUncompletedQuizzes().remove(quiz);

        int nextAttemptNumber = tracker.getNextAttemptNumberForQuiz(quiz.getQuizId());

        QuizAttemptRecord attempt = QuizAttemptRecord.builder()
                .quiz(quiz)
                .attemptNumber(nextAttemptNumber)
                .questionsAnswered(questionsAnswered)
                .correctAnswers(correctAnswers)
                .timeSpentSeconds(timeSpentSeconds)
                .gainedXp(gainedXp)
                .isQuizWon(isQuizWon)
                .isPerfectQuiz(isPerfectQuiz)
                .completedAt(Instant.now())
                .build();

        tracker.addAttemptRecord(attempt);

        stats.setTotalQuizzesCompleted(stats.getTotalQuizzesCompleted() + 1);
        if (isQuizWon) stats.setQuizzesWon(stats.getQuizzesWon() + 1);
        if (isPerfectQuiz) stats.setPerfectQuizzes(stats.getPerfectQuizzes() + 1);
        stats.setTotalQuestionsAnswered(stats.getTotalQuestionsAnswered() + questionsAnswered);
        stats.setTotalCorrectAnswers(stats.getTotalCorrectAnswers() + correctAnswers);
        stats.setTotalTimeSpentSeconds(stats.getTotalTimeSpentSeconds() + timeSpentSeconds);

        if (gainedXp > 0) {
            stats.setTotalXp(stats.getTotalXp() + gainedXp);
            stats.setCurrentLevel(1 + (stats.getTotalXp() / 1000));
            pointService.levelUser(userId, gainedXp);
        }

        int currentLevel = stats.getCurrentLevel();
        boolean leveledUp = currentLevel > previousLevel;

        LocalDate today = LocalDate.now();
        LocalDate lastActive = stats.getLastActivityDate();

        if (lastActive == null) {
            stats.setCurrentStreak(1);
        } else if (lastActive.plusDays(1).isEqual(today)) {
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
        } else if (!lastActive.isEqual(today)) {
            stats.setCurrentStreak(1);
        }

        if (stats.getCurrentStreak() > stats.getLongestStreak()) {
            stats.setLongestStreak(stats.getCurrentStreak());
        }
        stats.setLastActivityDate(today);

        userTrackerRepository.save(tracker);

        return SubmissionResultDto.builder()
                .scoreEarned(gainedXp)
                .correctCount(correctAnswers)
                .totalQuestions(totalQuestions)
                .currentXp(stats.getTotalXp())
                .currentLevel(currentLevel)
                .leveledUp(leveledUp)
                .build();
    }

    @Transactional
    public UserTrackerResponse history(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist with id: " + userId));

        UserTracker tracker = userTrackerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserTracker newTracker = UserTracker.builder()
                            .user(user)
                            .overallStats(new UserOverallStats())
                            .build();
                    return userTrackerRepository.save(newTracker);
                });

        UserOverallStats stats = tracker.getOverallStats();
        if (stats == null) {
            stats = new UserOverallStats();
            tracker.setOverallStats(stats);
        }

        // Calculate derived stats safely
        int totalXp = stats.getTotalXp() != null ? stats.getTotalXp() : 0;
        int currentLevel = stats.getCurrentLevel() != null ? stats.getCurrentLevel() : 1;
        int xpToNewLevel = Math.max(0, (currentLevel * 1000) - totalXp);

        int quizzesTaken = stats.getTotalQuizzesCompleted() != null ? stats.getTotalQuizzesCompleted() : 0;
        int quizzesWon = stats.getQuizzesWon() != null ? stats.getQuizzesWon() : 0;
        int totalAnswered = stats.getTotalQuestionsAnswered() != null ? stats.getTotalQuestionsAnswered() : 0;
        int totalCorrect = stats.getTotalCorrectAnswers() != null ? stats.getTotalCorrectAnswers() : 0;

        double winRate = quizzesTaken > 0 ? Math.round(((double) quizzesWon / quizzesTaken) * 10000.0) / 100.0 : 0.0;
        double accuracyRate = totalAnswered > 0 ? Math.round(((double) totalCorrect / totalAnswered) * 10000.0) / 100.0 : 0.0;

        // Map attempting records
        List<QuizAttemptResponse> attemptResponses = tracker.getAttemptRecords() != null
                ? tracker.getAttemptRecords().stream()
                  .map(record -> QuizAttemptResponse.builder()
                                 .quizId(record.getQuiz() != null ? record.getQuiz().getQuizId() : null)
                                 .attemptNumber(record.getAttemptNumber())
                                 .questionsAnswered(record.getQuestionsAnswered())
                                 .correctAnswers(record.getCorrectAnswers())
                                 .timeSpentSeconds(record.getTimeSpentSeconds())
                                 .gainedXp(record.getGainedXp())
                                 .isQuizWon(record.getIsQuizWon())
                                 .isPerfectQuiz(record.getIsPerfectQuiz())
                                 .completedAt(record.getCompletedAt())
                                 .build())
                  .collect(Collectors.toList())
                : Collections.emptyList();

        return UserTrackerResponse.builder()
                .id(tracker.getId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .totalXp(totalXp)
                .xpToNewLevel(xpToNewLevel)
                .currentLevel(currentLevel)
                .streak(stats.getCurrentStreak() != null ? stats.getCurrentStreak() : 0)
                .longestStreak(stats.getLongestStreak() != null ? stats.getLongestStreak() : 0)
                .quizzesTaken(quizzesTaken)
                .quizzesWon(quizzesWon)
                .perfectQuizzes(stats.getPerfectQuizzes() != null ? stats.getPerfectQuizzes() : 0)
                .totalQuestionsAnswered(totalAnswered)
                .totalCorrectAnswers(totalCorrect)
                .totalTimeSpentSeconds(stats.getTotalTimeSpentSeconds() != null ? stats.getTotalTimeSpentSeconds() : 0L)
                .lastActiveDate(stats.getLastActivityDate())
                .accuracyRate(accuracyRate)
                .winRate(winRate)
                .attemptRecords(attemptResponses)
                .completedQuizzes(tracker.getCompletedQuizzes() != null ? new ArrayList<>(tracker.getCompletedQuizzes()) : Collections.emptyList())
                .uncompletedQuizzes(tracker.getUncompletedQuizzes() != null ? new ArrayList<>(tracker.getUncompletedQuizzes()) : Collections.emptyList())
                .bookmarkedQuizzes(tracker.getBookmarkedQuizzes() != null ? new ArrayList<>(tracker.getBookmarkedQuizzes()) : Collections.emptyList())
                .build();
    }

    @Transactional
    public void saveUncompletedQuiz(int userId, int quizId, UncompletedDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist with id: " + userId));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz doesn't exist with id: " + quizId));

        UserTracker tracker = userTrackerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserTracker newTracker = UserTracker.builder()
                            .user(user)
                            .overallStats(new UserOverallStats())
                            .build();
                    return userTrackerRepository.save(newTracker);
                });

        // Initialize uncompleted list if null
        if (tracker.getUncompletedQuizzes() == null) {
            tracker.setUncompletedQuizzes(new ArrayList<>());
        }

        // Add to uncompleted if it's not already in the list
        boolean alreadyUncompleted = tracker.getUncompletedQuizzes().stream()
                .anyMatch(q -> q.getQuizId().equals(quizId));

        if (!alreadyUncompleted) {
            tracker.getUncompletedQuizzes().add(quiz);
        }

        // Note: If you have an entity to save the exact dto details (currentQuestion, percentage)
        // you would save it here. Otherwise, adding the Quiz to the uncompleted list is sufficient
        // for tracking it in the tracker.

        userTrackerRepository.save(tracker);
    }
}
