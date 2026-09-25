package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.BadgeDTO;
import com.skillstorm.skillstorm.dto.UserDTO;
import com.skillstorm.skillstorm.dto.UserRegister;
import com.skillstorm.skillstorm.dto.UserResponse;
import com.skillstorm.skillstorm.enums.Role;
import com.skillstorm.skillstorm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
                .attempts(Set.of())
                .roles(Set.of(Role.USER))
                .badges(Set.of())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public UserDTO mapToDto(User user) {
        if(user == null){
            throw new IllegalArgumentException("Unable to process User Object");
        }

        Set<BadgeDTO> badges = badgeMapper.mapToDto(user.getBadges());

        return new UserDTO(user.getUsername(),user.getXp(),badges);
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
