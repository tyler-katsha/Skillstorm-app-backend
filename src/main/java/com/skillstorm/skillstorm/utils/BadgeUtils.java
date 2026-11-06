package com.skillstorm.skillstorm.utils;

import com.skillstorm.skillstorm.enums.Kind;
import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.model.UserTracker;
import com.skillstorm.skillstorm.repository.BadgeRepository;
import com.skillstorm.skillstorm.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BadgeUtils {


    private final LeaderboardService leaderboardService;
    private final BadgeRepository badgeRepository;

    public int calculateProgressForKind(UserTracker tracker, Badge badge) {
        return switch (badge.getKind()) {
            case GAMES -> tracker.getOverallStats().getTotalQuizzesCompleted();
            case WINS -> tracker.getOverallStats().getQuizzesWon();
            case PERFECT -> tracker.getOverallStats().getPerfectQuizzes();
            case LEADERBOARD -> calculateLeaderboardProgress(tracker.getUser().getUserId(),badge.getDefinitionId()); // Requires separate logic to check leaderboard position
            case TOPICS -> calculateLeDistinctTopics(tracker); // Requires logic to count unique categories played
            case HIGH_SCORES -> calculateHighScores(tracker);
            default -> 0;
        };
    }

    private int calculateHighScores(UserTracker tracker) {
         if(tracker.getAttemptRecords() == null || tracker.getAttemptRecords().isEmpty()) {
             return 0;
         }

         return (int) tracker.getAttemptRecords().stream()
                 .filter(record -> {

                     if(record.getQuestionsAnswered() == null || record.getQuestionsAnswered() == 0 || record.getCorrectAnswers() == null) return false;

                     double percentage = ((double) record.getCorrectAnswers() / record.getQuestionsAnswered()) * 100;

                     return percentage >= 80.0;
                 }).count();
    }

    private int calculateLeDistinctTopics(UserTracker tracker) {
         if(tracker.getAttemptRecords() == null || tracker.getAttemptRecords().isEmpty()) return 0;

         return (int) tracker.getAttemptRecords().stream()
                 .filter(record -> record.getQuiz() != null && record.getQuiz().getTopics() != null)
                 .flatMap(record -> record.getQuiz().getTopics().stream())
                 .distinct()
                 .count();
    }
    private int calculateLeaderboardProgress(Integer userId,String definitionId) {

        final Kind kind;

        final int goal = badgeRepository.findGoalByDefinitionId(definitionId);

        try {
            kind = Kind.valueOf(definitionId.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return 0;
        }

        int rank = leaderboardService.getUserGlobalRank(userId);

        return switch(kind) {
            case TOP_10 ->  leaderboardService.isUserInTop10TenPercentDynamic(rank) ? goal : 0;
            case PODIUM_FINISH -> leaderboardService.getUserGlobalRank(userId) <= goal ? goal : 0;
            case GLOBAL_CHAMP -> leaderboardService.isGlobalChampion(userId) ? goal : 0;
            default -> 0;
        };

    }
}
