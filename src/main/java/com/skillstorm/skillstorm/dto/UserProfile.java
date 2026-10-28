package com.skillstorm.skillstorm.dto;

import com.skillstorm.skillstorm.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    private Integer userId;
    private String username;
    private String email;
    private Integer level;
    private Integer xp;
    private Set<Role> role;
}
