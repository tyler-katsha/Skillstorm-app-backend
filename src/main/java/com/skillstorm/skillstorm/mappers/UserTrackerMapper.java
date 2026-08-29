package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.UserTrackerRequest;
import com.skillstorm.skillstorm.dto.UserTrackerResponse;
import com.skillstorm.skillstorm.model.UserTracker;
import org.mapstruct.Mapper;

@Mapper
public interface UserTrackerMapper {

    UserTrackerResponse mapToDto(UserTracker tracker);
    UserTracker mapBackToObj(UserTrackerRequest request);
}
