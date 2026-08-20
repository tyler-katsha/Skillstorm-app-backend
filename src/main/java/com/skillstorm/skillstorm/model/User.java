package com.skillstorm.skillstorm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skillstorm.skillstorm.utils.RoleHelper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user")
@Data // Getter, Setter, toString, hashCode, etc...
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email",nullable = false,unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "xp", nullable = false)
    private int xp = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Badge> badges;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Attempt> attempts;

    // we are going to separate the role with a delimiter (:,;|)
    @Column(name = "roles",nullable = false)
    private String roles;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    @NullMarked
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return RoleHelper.convertFromStringToSet(roles).stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toSet());
    }


}
