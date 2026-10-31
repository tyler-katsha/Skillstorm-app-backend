package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.*;
import com.skillstorm.skillstorm.enums.Role;
import com.skillstorm.skillstorm.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Component
public class UserMapperImpl implements UserMapper{

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
                .createdAt(LocalDateTime.now())
                .build();
    }



    @Override
    public UserProfile mapToProfile(User user) {
        return UserProfile.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .level(user.getXp())
                .xp(user.getLevel())
                .roles(user.getRoles())
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
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}
