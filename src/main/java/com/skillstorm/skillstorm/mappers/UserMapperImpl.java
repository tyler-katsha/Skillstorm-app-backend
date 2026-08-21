package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.BadgeDTO;
import com.skillstorm.skillstorm.dto.UserDTO;
import com.skillstorm.skillstorm.dto.UserRegister;
import com.skillstorm.skillstorm.dto.UserResponse;
import com.skillstorm.skillstorm.enums.Role;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.utils.RoleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserMapperImpl implements UserMapper{
    @Autowired
    private BadgeMapper badgeMapper;

    @Override
    public User toUser(UserRegister request) {

        if(request == null){
            throw new IllegalArgumentException("Unable to process User Object");
        }

        String role = String.valueOf(Role.USER);
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .xp(0)
                .attempts(List.of())
                .roles(role + ":")
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
    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(RoleHelper.convertFromStringToSet(user.getRoles()))
                .xp(user.getXp())
                .attempts(user.getAttempts())
                .badges(user.getBadges())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}
