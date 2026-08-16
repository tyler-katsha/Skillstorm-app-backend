package com.skillstorm.skillstorm.dto;

import java.util.List;

record BadgeDTO (String name, String description) {}

public record UserDTO (String username, int xp, List<BadgeDTO> badges) {}
