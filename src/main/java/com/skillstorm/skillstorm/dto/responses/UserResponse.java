package com.skillstorm.skillstorm.dto.responses;

import com.skillstorm.skillstorm.enums.AuthProvider;
import com.skillstorm.skillstorm.enums.Role;
import com.skillstorm.skillstorm.model.Attempt;
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
    private Integer xp;
    private Integer level;
    private List<Attempt> attempts;
    private String createdAt;
    private AuthProvider authProvider;
}
