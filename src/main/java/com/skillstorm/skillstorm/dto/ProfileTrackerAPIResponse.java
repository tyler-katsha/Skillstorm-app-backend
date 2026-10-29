package com.skillstorm.skillstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileTrackerAPIResponse {

    private UserProfile user;
    private UserTrackerResponse userTrackerResponse;
    private Integer rank;
}
