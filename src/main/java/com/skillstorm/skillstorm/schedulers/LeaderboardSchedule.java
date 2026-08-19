package com.skillstorm.skillstorm.schedulers;

import com.skillstorm.skillstorm.model.Leaderboard;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.LeaderboardRepository;
import com.skillstorm.skillstorm.service.LeaderboardService;
import com.skillstorm.skillstorm.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardSchedule {

    private final LeaderboardService leaderboardService;
    private final UserService userService;
    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardSchedule(LeaderboardService leaderboardService,UserService userService,LeaderboardRepository leaderboardRepository){
        this.leaderboardService = leaderboardService;
        this.userService = userService;
        this.leaderboardRepository = leaderboardRepository;
    }

    @Scheduled(cron = "0 0 * * *") // Schedules an update at 12:00AM every day
    public void updateLeaderboard(){

        List<Leaderboard> existingLeaderboardUsers = leaderboardService.getAll();

        List<User> users = userService.findTop10ByOrderByXpDesc();

        for(int i = 0; i < existingLeaderboardUsers.size();i++){

            Leaderboard leaderboard = existingLeaderboardUsers.get(i);
            User user = users.get(i);

            leaderboard.setRank(i + 1); // 1..10
            leaderboard.setUser(user);
            leaderboard.setTotalScore(user.getXp());

            leaderboardRepository.save(leaderboard);

            System.out.println(String.format("Finished Processing Rank %d",(i+1)));
        }

        System.out.println("Leader board updated");
    }
}
