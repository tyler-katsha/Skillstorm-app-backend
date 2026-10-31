package com.skillstorm.skillstorm.utils;

import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.model.UserTracker;
import com.skillstorm.skillstorm.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class BadgeUtils {

    private static final String TOP_10 = "top-10",WEEKLY_CHAMP = "global-champ";

    private final LeaderboardService leaderboardService;

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
                 .map(record -> record.getQuiz().getTopics())
                 .distinct()
                 .count();
    }
    private int calculateLeaderboardProgress(Integer userId,String definitionId) {


        // If the goal is just "reach it once", you return 1 for true, 0 for false.
        if(TOP_10.equals(definitionId)) {
            boolean isTopTenPercent = leaderboardService.isUserInTop10TenPercentDynamic(userId);
            return isTopTenPercent ? 1 : 0;
        } else if (WEEKLY_CHAMP.equals(definitionId)) {
            boolean isGlobalChampion = leaderboardService.isGlobalChampion(userId);
            return isGlobalChampion ? 1 : 0;
        }
        return 0;
    }
}
