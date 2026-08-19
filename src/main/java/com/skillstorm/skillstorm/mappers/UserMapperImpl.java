package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.BadgeDTO;
import com.skillstorm.skillstorm.dto.UserDTO;
import com.skillstorm.skillstorm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapperImpl implements UserMapper{
    @Autowired
    private BadgeMapper badgeMapper;
    @Override
    public UserDTO mapToDto(User user) {
        if(user == null){
            throw new IllegalArgumentException("Unable to process User Object");
        }

        List<BadgeDTO> badges = badgeMapper.mapToDto(user.getBadges());

        return new UserDTO(user.getUsername(),user.getXp(),badges);
    }
}
