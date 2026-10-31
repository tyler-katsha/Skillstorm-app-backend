package com.skillstorm.skillstorm.dto.requests;

import com.skillstorm.skillstorm.enums.Kind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeRequest {

    private String definitionId;
    private String title;
    private String description;
    private String icon;
    private Kind kind;
    private Integer goal;
}
