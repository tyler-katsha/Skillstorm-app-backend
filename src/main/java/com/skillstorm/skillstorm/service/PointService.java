package com.skillstorm.skillstorm.service;

import com.skillstorm.skillstorm.dto.UserResponse;
import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.mappers.UserMapper;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final LocalCacheService cacheService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional
    public UserResponse levelUser(int userId,int gainedXp){
        var userKey = "user:"+ userId;
        var adminAllUsersKey = "admin:user:all";
        var allLeaderboard = "leaderboard:all";

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist with id: " + userId));

        int currentXp = user.getXp() != null ? user.getXp() : 0;
        int updatedXp = currentXp + gainedXp;
        int updatedLevel = calculateLevel(updatedXp);

        user.setXp(updatedXp);
        user.setLevel(updatedLevel);
        userRepository.save(user);

        UserResponse userResponse = userMapper.mapToResponse(user);

        cacheService.put(userKey,userResponse);
        cacheService.evict(adminAllUsersKey);
        cacheService.evict(allLeaderboard);

        return userResponse;
    }

    protected int calculateLevel(int xp){
        return (xp / 1000) + 1;
    }
}
