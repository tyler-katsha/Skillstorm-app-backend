package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.LeaderboardDto;
import com.skillstorm.skillstorm.model.Leaderboard;
import org.springframework.stereotype.Component;

@Component
public class LeaderboardMapperImpl implements LeaderboardMapper{
    @Override
    public LeaderboardDto mapToDto(Leaderboard leaderboard) {
        return LeaderboardDto
                .builder()
                .rank(leaderboard.getRank())
                .level(leaderboard.getUser().getXp())
                .username(leaderboard.getUser().getUsername())
                .points(leaderboard.getTotalScore())
                .build();
    }
}
