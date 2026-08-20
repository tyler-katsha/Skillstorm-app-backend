package com.skillstorm.skillstorm.service;

import com.skillstorm.skillstorm.dto.UserResponse;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.mappers.UserMapper;
import com.skillstorm.skillstorm.model.Leaderboard;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.LeaderboardRepository;
import com.skillstorm.skillstorm.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PointService {


    private final LocalCacheService cacheService;
    private final LeaderboardRepository leaderboardRepository;
    private final UserMapper userMapper;

    public PointService(LocalCacheService cacheService,LeaderboardRepository leaderboardRepository,UserMapper userMapper){
        this.cacheService = cacheService;
        this.leaderboardRepository = leaderboardRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponse levelUser(int userId,int gainedXp){
        String userKey = "user:"+ userId;
        String adminAllUsersKey = "admin:user:all";

        Leaderboard leaderboardUserVersion = leaderboardRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist"));

        User user = leaderboardUserVersion.getUser();
        int updatedXp = user.getXp() + gainedXp;
        int updatedLevel = calculateLevel(updatedXp);

        user.setXp(updatedXp);
        leaderboardUserVersion.getUser().setXp(updatedXp);
        leaderboardUserVersion.setTotalScore(updatedLevel);

        leaderboardRepository.save(leaderboardUserVersion);

        UserResponse userResponse = userMapper.mapToResponse(user);

        cacheService.put(userKey,userResponse);
        cacheService.evict(adminAllUsersKey);

        return userResponse;
    }

    private int calculateLevel(int xp){
        return (xp / 50) + 1;
    }
}
