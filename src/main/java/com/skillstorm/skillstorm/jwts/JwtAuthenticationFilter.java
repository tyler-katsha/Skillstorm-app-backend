package com.skillstorm.skillstorm.jwts;

import com.skillstorm.skillstorm.oauth.CustomUserDetailsService;
import com.skillstorm.skillstorm.oauth.UserPrincipal;
import com.skillstorm.skillstorm.utils.JwtHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try{
            String token = JwtHelper.extractToken(request);

            if(token != null) {

                int userId = jwtTokenProvider.extractUserId(token);

                if (userId != 0L && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = customUserDetailsService.loadByUserId(userId);

                    UserPrincipal principal = new UserPrincipal(userId,userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());

                    if (jwtTokenProvider.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        } catch (Exception e){
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request,response);
    }
}
