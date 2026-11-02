package com.skillstorm.skillstorm.dto.responses;

import com.skillstorm.skillstorm.enums.Kind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeResponse {

    private Integer id;
    private String definitionId;
    private String title;
    private String description;
    private String icon;
    private Kind kind;

    private Integer goal;
    private Integer currentProgress;

    private boolean isEarned;
    private Instant earnedAt;
    
    public BadgeResponse(Integer id, String title, String description, String icon, Integer goal, Integer currentProgress) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.goal = goal;
        this.currentProgress = currentProgress;
    }
}
