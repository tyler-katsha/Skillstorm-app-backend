package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.BadgeDTO;
import com.skillstorm.skillstorm.model.Badge;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BadgeMapperImpl implements BadgeMapper{

    @Override
    public Set<BadgeDTO> mapToDto(Set<Badge> badges) {
        return badges.stream()
                .map(badge -> new BadgeDTO(badge.getName(), badge.getDescription()))
                .collect(Collectors.toSet());
    }
}
