package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.BadgeDTO;
import com.skillstorm.skillstorm.model.Badge;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BadgeMapper {
    List<BadgeDTO> mapToDto(List<Badge> badges);
}
