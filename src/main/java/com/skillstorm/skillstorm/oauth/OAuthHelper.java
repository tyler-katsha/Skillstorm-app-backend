package com.skillstorm.skillstorm.oauth;

import com.skillstorm.skillstorm.jwts.JwtTokenProvider;
import com.skillstorm.skillstorm.model.User;
import com.skillstorm.skillstorm.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuthHelper extends SimpleUrlAuthenticationFailureHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public OAuthHelper(JwtTokenProvider tokenProvider,UserRepository userRepository){
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }


    public void issueTokenAndRedirect(HttpServletRequest request, HttpServletResponse response, User user) throws IOException{

        String token = tokenProvider.generateToken(user);

        String baseUrl = "http://localhost:5173/oauth2/redirect";

        String targetUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("token",token)
                .build()
                .encode()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    public void handleExceptionRedirect(HttpServletRequest request, HttpServletResponse response, String errorCode) throws IOException {
        // Redirect back to frontend login with an error query parameter instead of breaking the filter chain
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/login").queryParam("error", errorCode).build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    public String generateUniqueUsername(String resolvedRawName, String email) {

        String base;
        final String regex = "[^a-z0-9]";

        if(resolvedRawName != null && resolvedRawName.isBlank()){
            base = resolvedRawName.toLowerCase().replaceAll(regex,"");
        } else if(email != null && email.contains("@")){
            base = email.split("@")[0].toLowerCase().replaceAll(regex,"");
        } else{
            base = "user";
        }

        if(base.isBlank()){
            base = "user";
        }

        String candidate = base;

        int attempts = 0;

        // Loop and append short unique suffixes until an available username is found
        while (userRepository.existsByUsername(candidate) && attempts < 5) {
            String suffix = UUID.randomUUID().toString().substring(0, 4);
            candidate = base + "_" + suffix;
            attempts++;
        }

        // Final fallback in high-traffic or multiple collision scenarios
        if (userRepository.existsByUsername(candidate)) {
            candidate = "user_" + UUID.randomUUID().toString().substring(0, 8);
        }

        return candidate;

    }
    
}
