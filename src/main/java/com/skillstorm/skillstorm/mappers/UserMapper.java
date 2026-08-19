package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.UserDTO;
import com.skillstorm.skillstorm.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserDTO mapToDto(User user);
}
