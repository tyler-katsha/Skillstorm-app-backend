package com.skillstorm.skillstorm.service;

import com.skillstorm.skillstorm.dto.UserTrackerRequest;
import com.skillstorm.skillstorm.dto.UserTrackerResponse;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.mappers.UserTrackerMapper;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.model.UserTracker;
import com.skillstorm.skillstorm.repository.UserRepository;
import com.skillstorm.skillstorm.repository.UserTrackerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserTrackerService {

    private final UserTrackerRepository userTrackerRepository;
    private final UserRepository userRepository;
    private final PointService pointService;
    private final UserTrackerMapper mapper;

    public UserTrackerService(UserTrackerRepository userTrackerRepository,UserRepository userRepository,PointService pointService,UserTrackerMapper mapper){
        this.userTrackerRepository = userTrackerRepository;
        this.pointService = pointService;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional
    public UserTrackerResponse createTracker(UserTrackerRequest request){


        UserTracker tracker = mapper.mapBackToObj(request);

        User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        tracker.setUser(user);
        int gainedXp = request.getGainedXp();

        pointService.levelUser(request.getUserId(),gainedXp);

        userTrackerRepository.save(tracker);
        return mapper.mapToDto(tracker);
    }

    @Transactional
    public UserTracker updateTracker(UserTrackerRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("No request to map back to");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist with id: " + request.getUserId()));

        // Find existing tracker or initialize a new one for the user
        UserTracker tracker = userTrackerRepository.findByUserId(user.getUserId())
                .orElseGet(() -> UserTracker.builder()
                        .user(user)
                        .currentLevel(1)
                        .totalXp(0)
                        .streak(0)
                        .longestStreak(0)
                        .build());

        // 1. Update Lifetime Performance Counters
        tracker.setQuizzesTaken(tracker.getQuizzesTaken() + 1);

        if (Boolean.TRUE.equals(request.getIsQuizWon())) {
            tracker.setQuizzesWon(tracker.getQuizzesWon() + 1);
        }

        if (Boolean.TRUE.equals(request.getIsPerfectQuiz())) {
            tracker.setPerfectQuizzes(tracker.getPerfectQuizzes() + 1);
        }

        // 2. Update Question-Level Metrics
        if (request.getQuestionsAnswered() != null) {
            tracker.setTotalQuestionsAnswered(tracker.getTotalQuestionsAnswered() + request.getQuestionsAnswered());
        }
        if (request.getCorrectAnswers() != null) {
            tracker.setTotalCorrectAnswers(tracker.getTotalCorrectAnswers() + request.getCorrectAnswers());
        }
        if (request.getTimeSpentSeconds() != null) {
            tracker.setTotalTimeSpentSeconds(tracker.getTotalTimeSpentSeconds() + request.getTimeSpentSeconds());
        }

        // 3. Update XP and Level
        if (request.getGainedXp() != null && request.getGainedXp() > 0) {
            tracker.setTotalXp(tracker.getTotalXp() + request.getGainedXp());
            // Simple level formula: Level = 1 + (totalXp / 1000)
            tracker.setCurrentLevel(1 + (tracker.getTotalXp() / 1000));
        }

        // 4. Update Daily Streak & Activity Date
        LocalDate today = LocalDate.now();
        LocalDate lastActive = tracker.getLastActiveDate();

        if (lastActive == null) {
            tracker.setStreak(1);
        } else if (lastActive.plusDays(1).isEqual(today)) {
            // Active on the next consecutive day
            tracker.setStreak(tracker.getStreak() + 1);
        } else if (!lastActive.isEqual(today)) {
            // Streak broken
            tracker.setStreak(1);
        }

        if (tracker.getStreak() > tracker.getLongestStreak()) {
            tracker.setLongestStreak(tracker.getStreak());
        }
        tracker.setLastActiveDate(today);

        // 5. Update Quiz Relationships
        if (request.getCompletedQuizId() != null) {
            Quiz completedQuiz = quizRepository.findById(request.getCompletedQuizId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + request.getCompletedQuizId()));

            tracker.getCompletedQuizzes().add(completedQuiz);
            tracker.getUncompletedQuizzes().remove(completedQuiz);
        }

        if (request.getBookmarkedQuizIds() != null && !request.getBookmarkedQuizIds().isEmpty()) {
            Set<Quiz> bookmarks = new HashSet<>(quizRepository.findAllById(request.getBookmarkedQuizIds()));
            tracker.setBookmarkedQuizzes(bookmarks);
        }

        return userTrackerRepository.save(tracker);
    }
}
