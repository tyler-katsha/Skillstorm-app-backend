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
        String username = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email)
                .map(existingUser -> {

                    if(username != null && !username.isBlank()){
                        existingUser.setUsername(username);
                    }

                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .xp(0)
                                .roles(Role.USER + ":")
                                .username(username)
                                .email(email)
                                .createdAt(LocalDateTime.now())
                                .attempts(List.of())
                                .badges(List.of())
                                .build()
                ));

        oAuthHelper.issueTokenAndRedirect(request,response,user);

    }

}
