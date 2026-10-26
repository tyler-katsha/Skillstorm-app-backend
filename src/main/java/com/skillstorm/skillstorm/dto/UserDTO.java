package com.skillstorm.skillstorm.dto;

import java.util.List;

public record UserDTO (String username, int xp, List<BadgeDTO> badges) {}
