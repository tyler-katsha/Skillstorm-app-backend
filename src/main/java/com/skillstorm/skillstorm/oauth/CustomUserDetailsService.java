package com.skillstorm.skillstorm.oauth;

import com.skillstorm.skillstorm.exceptions.ResourceNotFoundException;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;
import com.skillstorm.skillstorm.utils.RoleHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository repository){
        this.userRepository = repository;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist"));

        return UserPrincipal.builder()
                .password(user.getPassword())
                .username(user.getEmail())
                .authorities(RoleHelper.convertFromStringToSet(user.getRoles()).stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toSet()))
                .build();
    }
    public UserDetails loadByUserId(int userId) throws UsernameNotFoundException{
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User doesn't exist"));

        return UserPrincipal.builder()
                .password(user.getPassword())
                .username(user.getEmail())
                .authorities(RoleHelper.convertFromStringToSet(user.getRoles()).stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toSet()))
                .build();
    }


}
