package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.UserDTO;
import com.skillstorm.skillstorm.dto.UserRegister;
import com.skillstorm.skillstorm.dto.UserResponse;
import com.skillstorm.skillstorm.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface UserMapper {

    @Mapping(target="password",ignore = true)
    @Mapping(target = "roles",constant = "ROLE_USER")
    User toUser(UserRegister request);

    UserDTO mapToDto(User user);
    UserResponse mapToResponse(User user);
}
