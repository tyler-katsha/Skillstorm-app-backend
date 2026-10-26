package com.skillstorm.skillstorm.dto;

import com.skillstorm.skillstorm.enums.Role;
import com.skillstorm.skillstorm.model.Attempt;
import com.skillstorm.skillstorm.model.Badge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String username;
    private String email;
    private Set<Role> roles;
    private int xp;
    private List<Attempt> attempts;
    private List<Badge> badges;
    private String createdAt;

}
