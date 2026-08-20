package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.LeaderboardDto;
import com.skillstorm.skillstorm.model.Leaderboard;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeaderboardMapper {
    LeaderboardDto mapToDto(Leaderboard leaderboard);
}
