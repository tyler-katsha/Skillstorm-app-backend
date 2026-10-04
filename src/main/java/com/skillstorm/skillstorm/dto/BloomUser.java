package com.skillstorm.skillstorm.dto;

import com.skillstorm.skillstorm.enums.FilterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloomUser {
    private String type;
    private String value;
}
