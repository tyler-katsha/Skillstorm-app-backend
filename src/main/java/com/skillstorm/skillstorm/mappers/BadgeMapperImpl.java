package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.BadgeRequest;
import com.skillstorm.skillstorm.dto.BadgeResponse;
import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.model.UserBadge;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BadgeMapperImpl implements BadgeMapper{

    @Override
    public Badge toBadge(BadgeRequest request) {
        return Badge.builder()
                .definitionId(request.getDefinitionId())
                .title(request.getTitle())
                .description(request.getDescription())
                .icon(request.getIcon())
                .kind(request.getKind())
                .goal(request.getGoal())
                .build();
    }

    @Override
    public BadgeResponse mapToBadgeResponse(Badge badge) {
        return BadgeResponse.builder()
                .id(badge.getId())
                .definitionId(badge.getDefinitionId())
                .title(badge.getTitle())
                .description(badge.getDescription())
                .icon(badge.getIcon())
                .kind(badge.getKind())
                .goal(badge.getGoal())
                .build();
    }

    @Override
    public BadgeResponse mapToBadgeResponse(UserBadge userBadge) {
        return null;
    }

    @Override
    public List<BadgeResponse> mapToBadgeResponses(List<Badge> badges) {
        return badges.stream()
                .map(this::mapToBadgeResponse)
                .toList();

    }
}
