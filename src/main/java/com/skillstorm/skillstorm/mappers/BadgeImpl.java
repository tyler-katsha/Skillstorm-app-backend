package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.BadgeDTO;
import com.skillstorm.skillstorm.model.Badge;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BadgeImpl implements BadgeMapper{

    @Override
    public List<BadgeDTO> mapToDto(List<Badge> badges) {
        return badges.stream()
                .map(badge -> new BadgeDTO(badge.getName(), badge.getDescription()))
                .toList();
    }
}
