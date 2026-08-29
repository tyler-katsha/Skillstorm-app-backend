package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.QuizSummaryDto;
import com.skillstorm.skillstorm.dto.UserResponse;
import com.skillstorm.skillstorm.dto.UserTrackerRequest;
import com.skillstorm.skillstorm.dto.UserTrackerResponse;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.model.Quiz;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.model.UserTracker;
import com.skillstorm.skillstorm.repository.QuizRepository;
import com.skillstorm.skillstorm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserTrackerMapperImpl implements UserTrackerMapper{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Override
    public UserTrackerResponse mapToDto(UserTracker tracker) {
        if(tracker == null) throw new IllegalArgumentException("No Tracker found");

        int xpForNext = (tracker.getCurrentLevel() * 50) - tracker.getTotalXp();

        return UserTrackerResponse.builder()
                .id(tracker.getId())
                .userId(tracker.getUser() != null ? tracker.getUser().getUserId() : null)
                .username(tracker.getUser() != null ? tracker.getUser().getUsername() : null)
                .currentLevel(tracker.getCurrentLevel())
                .totalXp(tracker.getTotalXp())
                .xpToNewLevel(Math.max(0, xpForNext))
                .streak(tracker.getStreak())
                .longestStreak(tracker.getLongestStreak())
                .lastActiveDate(tracker.getLastActiveDate())
                .totalTimeSpentSeconds(tracker.getTotalTimeSpentSeconds())
                .quizzesTaken(tracker.getQuizzesTaken())
                .quizzesWon(tracker.getQuizzesWon())
                .perfectQuizzes(tracker.getPerfectQuizzes())
                .winRate(tracker.getWinRate())
                .totalQuestionsAnswered(tracker.getTotalQuestionsAnswered())
                .totalCorrectAnswers(tracker.getTotalCorrectAnswers())
                .accuraryRate(tracker.getAccuracyRate())
                .completedQuizzes(mapQuizSet(tracker.getCompletedQuizzes()))
                .uncompletedQuizzes(mapQuizSet(tracker.getUncompletedQuizzes()))
                .bookmarkedQuizzes(mapQuizSet(tracker.getBookmarkedQuizzes()))
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

        Set<Quiz> uncompleted = (request.getUncompletedQuizIds() != null && !request.getUncompletedQuizIds().isEmpty())
                ? new HashSet<>(quizRepository.findAllById(request.getUncompletedQuizIds()))
                : new HashSet<>();

        Set<Quiz> bookmarked = (request.getBookmarkedQuizIds() != null && !request.getBookmarkedQuizIds().isEmpty())
                ? new HashSet<>(quizRepository.findAllById(request.getBookmarkedQuizIds()))
                : new HashSet<>();

        return UserTracker.builder()
                .user(user)
                .quizzesTaken(1)
                .quizzesWon(Boolean.TRUE.equals(request.getIsQuizWon()) ? 1 : 0)
                .perfectQuizzes(Boolean.TRUE.equals(request.getIsPerfectQuiz()) ? 1 : 0)
                .totalQuestionsAnswered(request.getQuestionsAnswered() != null ? request.getQuestionsAnswered() : 0)
                .totalCorrectAnswers(request.getCorrectAnswers() != null ? request.getCorrectAnswers() : 0)
                .totalTimeSpentSeconds(request.getTimeSpentSeconds() != null ? request.getTimeSpentSeconds() : 0L)
                .totalXp(request.getGainedXp() != null ? request.getGainedXp() : 0)
                .currentLevel(request.getCurrentLevel() != null ? request.getCurrentLevel() : 1)
                .streak(1)
                .longestStreak(1)
                .lastActiveDate(LocalDate.now())
                .completedQuizzes(completed)
                .uncompletedQuizzes(uncompleted)
                .bookmarkedQuizzes(bookmarked)
                .build();
    }

    private Set<QuizSummaryDto> mapQuizSet(Set<Quiz> quizzes){
        if(quizzes == null) return Set.of();

        return quizzes.stream()
                .map(q -> QuizSummaryDto.builder()
                        .id(q.getQuizId())
                        .title(q.getTitle())
                        .build())
                .collect(Collectors.toSet());
    }
}
