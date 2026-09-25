package com.skillstorm.skillstorm.dto;

import java.util.Set;

public record UserDTO (String username, int xp, Set<BadgeDTO> badges) {}
