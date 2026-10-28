package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.*;
import com.skillstorm.skillstorm.enums.Role;
import com.skillstorm.skillstorm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Component
public class UserMapperImpl implements UserMapper{
    @Autowired
    private BadgeMapper badgeMapper;

    @Override
    public User toUser(UserRegister request) {

        if(request == null){
            throw new IllegalArgumentException("Unable to process User Object");
        }

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .xp(0)
                .attempts(List.of())
                .roles(Set.of(Role.USER))
                .badges(List.of())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public UserDTO mapToDto(User user) {
        if(user == null){
            throw new IllegalArgumentException("Unable to process User Object");
        }

        List<BadgeDTO> badges = badgeMapper.mapToDto(user.getBadges());

        return new UserDTO(user.getUsername(),user.getXp(),badges);
    }

    @Override
    public UserProfile mapToProfile(User user) {
        return UserProfile.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .level(user.getXp())
                .xp(user.getLevel())
                .role(user.getRoles())
                .build();
    }

    @Override
    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .xp(user.getXp())
                .attempts(user.getAttempts())
                .badges(user.getBadges())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}
