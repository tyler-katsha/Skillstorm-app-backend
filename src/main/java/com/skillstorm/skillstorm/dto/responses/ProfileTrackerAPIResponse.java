package com.skillstorm.skillstorm.dto.responses;

import com.skillstorm.skillstorm.dto.extraDtos.UserProfile;
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
