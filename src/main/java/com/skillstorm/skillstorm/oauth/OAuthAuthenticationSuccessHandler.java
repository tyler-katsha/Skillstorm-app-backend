package com.skillstorm.skillstorm.oauth;

import com.skillstorm.skillstorm.enums.Role;
import com.skillstorm.skillstorm.jwts.JwtTokenProvider;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private OAuthHelper oAuthHelper;
    @Autowired
    private UserRepository userRepository;
    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();


        String email = oAuth2User.getAttribute("email");

        if(email == null || email.isBlank()){
            oAuthHelper.handleExceptionRedirect(request, response, "oauth_cancelled");
            return;
        }

        // extract data from OAuth Provider like Google, Facebook, etc...
        String rawName = oAuth2User.getAttribute("name");

        if(rawName == null || rawName.isBlank()){
            rawName = oAuth2User.getAttribute("login"); // GitHub fallback
        }

        final String resolvedRawName = rawName;

        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    // User already exists; keep existing unique username to avoid overwrite collisions
                    return userRepository.save(existingUser);
                })
                // Generate a collision-free username for the new registration
                .orElseGet(() -> {
                    String uniqueUsername = oAuthHelper.generateUniqueUsername(resolvedRawName, email);
                    return userRepository.save(
                            User.builder()
                                    .xp(0)
                                    .roles(Role.USER + ":")
                                    .username(uniqueUsername)
                                    .email(email)
                                    .createdAt(LocalDateTime.now())
                                    .attempts(List.of())
                                    .badges(List.of())
                                    .build());
                });

        oAuthHelper.issueTokenAndRedirect(request,response,user);

    }

}
