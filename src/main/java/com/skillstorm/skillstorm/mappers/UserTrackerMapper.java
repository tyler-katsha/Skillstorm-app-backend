package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.ProfileTrackerAPIResponse;
import com.skillstorm.skillstorm.dto.QuizAttemptResponse;
import com.skillstorm.skillstorm.dto.UserTrackerRequest;
import com.skillstorm.skillstorm.dto.UserTrackerResponse;
import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.QuizAttemptRecord;
import com.skillstorm.skillstorm.model.UserTracker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface UserTrackerMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "totalXp", source = "overallStats.totalXp")
    @Mapping(target = "currentLevel", source = "overallStats.currentLevel")
    @Mapping(target = "streak", source = "overallStats.currentStreak")
    @Mapping(target = "longestStreak", source = "overallStats.longestStreak")
    @Mapping(target = "quizzesTaken", source = "overallStats.totalQuizzesCompleted")
    @Mapping(target = "quizzesWon", source = "overallStats.quizzesWon")
    @Mapping(target = "perfectQuizzes", source = "overallStats.perfectQuizzes")
    @Mapping(target = "totalQuestionsAnswered", source = "overallStats.totalQuestionsAnswered")
    @Mapping(target = "totalCorrectAnswers", source = "overallStats.totalCorrectAnswers")
    @Mapping(target = "totalTimeSpentSeconds", source = "overallStats.totalTimeSpentSeconds")
    @Mapping(target = "lastActiveDate", source = "overallStats.lastActivityDate")
    @Mapping(target = "accuracyRate", expression = "java(tracker.getOverallStats() != null ? tracker.getOverallStats().getAccuracyRate() : 0.0)")
    @Mapping(target = "winRate", expression = "java(tracker.getOverallStats() != null ? tracker.getOverallStats().getWinRate() : 0.0)")
    @Mapping(target = "uncompletedQuizIds", source = "uncompletedQuizzes", qualifiedByName = "quizzesToIds")
    @Mapping(target = "completedQuizIds", source = "completedQuizzes", qualifiedByName = "quizzesToIds")
    @Mapping(target = "bookmarkedQuizIds", source = "bookmarkedQuizzes", qualifiedByName = "quizzesToIds")
    @Mapping(target = "attemptRecords", source = "attemptRecords")
    UserTrackerResponse mapToDto(UserTracker tracker);

    ProfileTrackerAPIResponse mapToHistory(UserTrackerResponse trackerResponse);

    @Mapping(target = "quizId", source = "quiz.id")
    QuizAttemptResponse mapAttemptToDto(QuizAttemptRecord record);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "attemptRecords", ignore = true)
    @Mapping(target = "uncompletedQuizzes", ignore = true)
    @Mapping(target = "completedQuizzes", ignore = true)
    @Mapping(target = "bookmarkedQuizzes", ignore = true)
    @Mapping(target = "overallStats.totalXp", source = "gainedXp", defaultExpression = "java(0)")
    @Mapping(target = "overallStats.currentLevel", constant = "1")
    @Mapping(target = "overallStats.currentStreak", constant = "0")
    @Mapping(target = "overallStats.longestStreak", constant = "0")
    @Mapping(target = "overallStats.totalQuizzesCompleted", constant = "0")
    @Mapping(target = "overallStats.quizzesWon", constant = "0")
    @Mapping(target = "overallStats.perfectQuizzes", constant = "0")
    @Mapping(target = "overallStats.totalQuestionsAnswered", source = "questionsAnswered", defaultExpression = "java(0)")
    @Mapping(target = "overallStats.totalCorrectAnswers", source = "correctAnswers", defaultExpression = "java(0)")
    @Mapping(target = "overallStats.totalTimeSpentSeconds", source = "timeSpentSeconds", defaultExpression = "java(0L)")
    @Mapping(target = "overallStats.lastActivityDate", expression = "java(java.time.LocalDate.now())")
    UserTracker mapBackToObj(UserTrackerRequest request);
}
