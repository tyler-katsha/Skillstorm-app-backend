package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.extraDtos.UserProfile;
import com.skillstorm.skillstorm.dto.requests.UserRegister;
import com.skillstorm.skillstorm.dto.responses.UserResponse;
import com.skillstorm.skillstorm.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface UserMapper {

    @Mapping(target="password",ignore = true)
    @Mapping(target = "roles",constant = "ROLE_USER")
    User toUser(UserRegister request);

    UserProfile mapToProfile(User user);
    UserResponse mapToResponse(User user);
}
