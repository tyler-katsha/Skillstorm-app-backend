package com.skillstorm.skillstorm.service;

import com.skillstorm.skillstorm.dto.*;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.model.*;
import com.skillstorm.skillstorm.repository.QuizRepository;
import com.skillstorm.skillstorm.repository.UserRepository;
import com.skillstorm.skillstorm.repository.UserTrackerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTrackerService {

    private static final int NOTHING = 0;
    private static final int MULTIPLIER = 10;
    private static final int QUIZ_WON = 50;
    private static final int PERFECT_QUIZ = 50;
    private static final int ONE = 1;
    private static final int XP_PER_LEVEL_STEP = 1000;

    private static final double WINNING_THRESHOLD = 0.70;
    private static final double PERCENTAGE_SCALE_FACTOR = 10000.0;
    private static final double DECIMAL_PERCENTAGE_DIVISOR = 100.0;
    private static final double DEFAULT_RATE = 0.0;

    private final UserRepository userRepository;
    private final UserTrackerRepository userTrackerRepository;
    private final QuizRepository quizRepository;
    private final PointService pointService;

    @CacheEvict(value = "top10Leaderboard", key = "'top10'")
    @Transactional
    public SubmissionResultDto submit(int id, SubmissionPayload payload, int userId) {
        try {
            if (payload == null) {
                throw new IllegalArgumentException("Payload cannot be null");
            }

            Quiz quiz = quizRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Quiz doesn't exist with id: " + id));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist with id: " + userId));

            payload.setQuizId(id);
            payload.setUserId(userId);

            List<UserAnswerSubmission> submittedAnswers = payload.getAnswers() != null ? payload.getAnswers() : Collections.emptyList();

            int totalQuestions = (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) ? quiz.getQuestions().size() : submittedAnswers.size();
            int questionsAnswered = submittedAnswers.size();
            int correctAnswers = NOTHING;

            // Guard against null questionId or null selectedAnswerId which crashes Collectors.toMap
            Map<Integer, Integer> answerMap = submittedAnswers.stream()
                    .filter(Objects::nonNull)
                    .filter(a -> a.getQuestionId() != null && a.getSelectedAnswerId() != null)
                    .collect(Collectors.toMap(UserAnswerSubmission::getQuestionId, UserAnswerSubmission::getSelectedAnswerId, (existing, replacement) -> replacement));

            if (quiz.getQuestions() != null) {
                for (Question question : quiz.getQuestions()) {
                    Integer selectedId = answerMap.get(question.getQuestionId());
                    if (selectedId != null && question.getAnswers() != null) {
                        boolean isCorrect = question.getAnswers().stream()
                                .anyMatch(a -> Objects.equals(a.getAnswerId(), selectedId) && Boolean.TRUE.equals(a.isCorrect()));
                        if (isCorrect) {
                            correctAnswers++;
                        }
                    }
                }
            }

            boolean isPerfectQuiz = totalQuestions > NOTHING && correctAnswers == totalQuestions;
            boolean isQuizWon = totalQuestions > NOTHING && ((double) correctAnswers / totalQuestions) >= WINNING_THRESHOLD;

            int gainedXp = (correctAnswers * MULTIPLIER) + (isQuizWon ? QUIZ_WON : NOTHING) + (isPerfectQuiz ? PERFECT_QUIZ : NOTHING);
            long timeSpentSeconds = payload.getTimeSpentSeconds();

            UserTracker tracker = getOrCreateTracker(user);

            UserOverallStats stats = tracker.getOverallStats();
            if (stats == null) {
                stats = new UserOverallStats();
            }

            initializeStatsIfNull(stats);

            int previousLevel = stats.getCurrentLevel();

            if (tracker.getCompletedQuizzes() == null) tracker.setCompletedQuizzes(new HashSet<>());
            if (tracker.getUncompletedQuizzes() == null) tracker.setUncompletedQuizzes(new HashSet<>());
            if (tracker.getBookmarkedQuizzes() == null) tracker.setBookmarkedQuizzes(new HashSet<>());
            if (tracker.getAttemptRecords() == null) tracker.setAttemptRecords(new ArrayList<>());

            tracker.getCompletedQuizzes().add(quiz);
            tracker.removeUncompletedProgress(quiz.getQuizId());

            int nextAttemptNumber = tracker.getNextAttemptNumberForQuiz(quiz.getQuizId());

            QuizAttemptRecord attempt = QuizAttemptRecord.builder()
                    .userTracker(tracker)
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

            stats.setTotalQuizzesCompleted(stats.getTotalQuizzesCompleted() + ONE);
            if (isQuizWon) stats.setQuizzesWon(stats.getQuizzesWon() + ONE);
            if (isPerfectQuiz) stats.setPerfectQuizzes(stats.getPerfectQuizzes() + ONE);
            stats.setTotalQuestionsAnswered(stats.getTotalQuestionsAnswered() + questionsAnswered);
            stats.setTotalCorrectAnswers(stats.getTotalCorrectAnswers() + correctAnswers);
            stats.setTotalTimeSpentSeconds(stats.getTotalTimeSpentSeconds() + timeSpentSeconds);

            // Calculate expected XP & Level as fallback in case PointService returns stale/0 values
            int expectedXp = stats.getTotalXp() + gainedXp;
            UserResponse updatedUser = pointService.levelUser(userId, gainedXp);

            int finalXp = (updatedUser != null && updatedUser.getXp() != null && updatedUser.getXp() >= expectedXp) ? updatedUser.getXp() : expectedXp;

            int expectedLevel = (finalXp / XP_PER_LEVEL_STEP) + ONE;
            int finalLevel = (updatedUser != null && updatedUser.getLevel() != null && updatedUser.getLevel() >= expectedLevel) ? updatedUser.getLevel() : expectedLevel;

            stats.setTotalXp(finalXp);
            stats.setCurrentLevel(finalLevel);

            // Keep User entity synchronized in DB
            user.setXp(finalXp);
            user.setLevel(finalLevel);
            userRepository.save(user);

            LocalDate today = LocalDate.now();
            LocalDate lastActive = stats.getLastActivityDate();

            if (lastActive == null) {
                stats.setCurrentStreak(ONE);
            } else if (lastActive.plusDays(ONE).isEqual(today)) {
                stats.setCurrentStreak(stats.getCurrentStreak() + ONE);
            } else if (!lastActive.isEqual(today)) {
                stats.setCurrentStreak(ONE);
            }

            if (stats.getCurrentStreak() > stats.getLongestStreak()) {
                stats.setLongestStreak(stats.getCurrentStreak());
            }
            stats.setLastActivityDate(today);

            tracker.setOverallStats(stats);
            userTrackerRepository.saveAndFlush(tracker);

            int currentLevel = stats.getCurrentLevel();
            boolean leveledUp = currentLevel > previousLevel;

            return SubmissionResultDto.builder()
                    .scoreEarned(gainedXp)
                    .correctCount(correctAnswers)
                    .totalQuestions(totalQuestions)
                    .currentXp(stats.getTotalXp())
                    .currentLevel(currentLevel)
                    .leveledUp(leveledUp)
                    .build();
        } catch(Exception e) {
            return null;
        }
    }

    @Transactional
    public UserTrackerResponse history(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist with id: " + userId));

        UserTracker tracker = getOrCreateTracker(user);

        UserOverallStats stats = tracker.getOverallStats();
        if (stats == null) {
            stats = new UserOverallStats();
            tracker.setOverallStats(stats);
        }
        initializeStatsIfNull(stats);

        int totalXp = stats.getTotalXp() != NOTHING ? stats.getTotalXp() : (user.getXp() != null ? user.getXp() : NOTHING);
        int currentLevel = stats.getCurrentLevel() != ONE ? stats.getCurrentLevel() : (user.getLevel() != null && user.getLevel() > NOTHING ? user.getLevel() : ONE);
        int xpToNewLevel = Math.max(NOTHING, (currentLevel * XP_PER_LEVEL_STEP) - totalXp);

        int quizzesTaken = stats.getTotalQuizzesCompleted();
        int quizzesWon = stats.getQuizzesWon();
        int totalAnswered = stats.getTotalQuestionsAnswered();
        int totalCorrect = stats.getTotalCorrectAnswers();

        double winRate = quizzesTaken > NOTHING ? Math.round(((double) quizzesWon / quizzesTaken) * PERCENTAGE_SCALE_FACTOR) / DECIMAL_PERCENTAGE_DIVISOR : DEFAULT_RATE;
        double accuracyRate = totalAnswered > NOTHING ? Math.round(((double) totalCorrect / totalAnswered) * PERCENTAGE_SCALE_FACTOR) / DECIMAL_PERCENTAGE_DIVISOR : DEFAULT_RATE;

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

        Set<UncompletedQuizProgress> uncompletedQuizSet = tracker.getUncompletedQuizzes() != null ? new HashSet<>(tracker.getUncompletedQuizzes()) : Collections.emptySet();
        Set<Quiz> bookmarkedQuizSet = tracker.getBookmarkedQuizzes() != null ? new HashSet<>(tracker.getBookmarkedQuizzes()) : Collections.emptySet();
        Set<Quiz> completedQuizSet = tracker.getCompletedQuizzes() != null ? new HashSet<>(tracker.getCompletedQuizzes()) : Collections.emptySet();

        return UserTrackerResponse.builder()
                .id(tracker.getId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .totalXp(totalXp)
                .xpToNewLevel(xpToNewLevel)
                .currentLevel(currentLevel)
                .streak(stats.getCurrentStreak())
                .longestStreak(stats.getLongestStreak())
                .quizzesTaken(quizzesTaken)
                .quizzesWon(quizzesWon)
                .perfectQuizzes(stats.getPerfectQuizzes())
                .totalQuestionsAnswered(totalAnswered)
                .totalCorrectAnswers(totalCorrect)
                .totalTimeSpentSeconds(stats.getTotalTimeSpentSeconds())
                .lastActiveDate(stats.getLastActivityDate())
                .accuracyRate(accuracyRate)
                .winRate(winRate)
                .attemptRecords(attemptResponses)
                .completedQuizzes(completedQuizSet)
                .uncompletedQuizzes(uncompletedQuizSet)
                .bookmarkedQuizzes(bookmarkedQuizSet)
                .build();
    }

    @Transactional
    public void saveUncompletedQuiz(int userId, int quizId, UncompletedDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UncompletedDto cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist with id: " + userId));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz doesn't exist with id: " + quizId));

        UserTracker tracker = getOrCreateTracker(user);

        tracker.addOrUpdateUncompletedProgress(quiz, dto.getCurrentQuestion(), dto.getTotalQuestions(), dto.getPercentage());

        userTrackerRepository.saveAndFlush(tracker);
    }

    private UserTracker getOrCreateTracker(User user) {
        return userTrackerRepository.findByUserId(user.getUserId())
                .orElseGet(() -> {
                    UserOverallStats initialStats = new UserOverallStats();
                    initializeStatsIfNull(initialStats);
                    if (user.getXp() != null) initialStats.setTotalXp(user.getXp());
                    if (user.getLevel() != null && user.getLevel() > NOTHING) initialStats.setCurrentLevel(user.getLevel());

                    UserTracker newTracker = UserTracker.builder()
                            .user(user)
                            .overallStats(initialStats)
                            .completedQuizzes(new HashSet<>())
                            .uncompletedQuizzes(new HashSet<>())
                            .bookmarkedQuizzes(new HashSet<>())
                            .attemptRecords(new ArrayList<>())
                            .build();
                    return userTrackerRepository.saveAndFlush(newTracker);
                });
    }

    private void initializeStatsIfNull(UserOverallStats stats) {
        if (stats.getTotalXp() == null) stats.setTotalXp(NOTHING);
        if (stats.getCurrentLevel() == null) stats.setCurrentLevel(ONE);
        if (stats.getTotalQuizzesCompleted() == null) stats.setTotalQuizzesCompleted(NOTHING);
        if (stats.getQuizzesWon() == null) stats.setQuizzesWon(NOTHING);
        if (stats.getPerfectQuizzes() == null) stats.setPerfectQuizzes(NOTHING);
        if (stats.getTotalQuestionsAnswered() == null) stats.setTotalQuestionsAnswered(NOTHING);
        if (stats.getTotalCorrectAnswers() == null) stats.setTotalCorrectAnswers(NOTHING);
        if (stats.getTotalTimeSpentSeconds() == null) stats.setTotalTimeSpentSeconds((long) NOTHING);
        if (stats.getCurrentStreak() == null) stats.setCurrentStreak(NOTHING);
        if (stats.getLongestStreak() == null) stats.setLongestStreak(NOTHING);
    }
}