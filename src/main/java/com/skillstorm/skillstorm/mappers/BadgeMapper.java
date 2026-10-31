package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.BadgeRequest;
import com.skillstorm.skillstorm.dto.BadgeResponse;
import com.skillstorm.skillstorm.model.Badge;
import com.skillstorm.skillstorm.model.UserBadge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel="spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BadgeMapper {

    Badge toBadge(BadgeRequest request);
    BadgeResponse mapToBadgeResponse(Badge badge);
    BadgeResponse mapToBadgeResponse(UserBadge userBadge);
    List<BadgeResponse> mapToBadgeResponses(List<Badge> badges);
}
