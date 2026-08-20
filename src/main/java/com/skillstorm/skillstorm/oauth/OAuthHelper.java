package com.skillstorm.skillstorm.oauth;

import com.skillstorm.skillstorm.jwts.JwtTokenProvider;
import com.skillstorm.skillstorm.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuthHelper extends SimpleUrlAuthenticationFailureHandler {

    private final JwtTokenProvider tokenProvider;

    public OAuthHelper(JwtTokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    public void issueTokenAndRedirect(HttpServletRequest request, HttpServletResponse response, User user) throws IOException{

        String token = tokenProvider.generateToken(user);

        String baseUrl = "http://localhost:5173/oauth2/redirect";

        String targetUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("token",token)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    public void handleExceptionRedirect(HttpServletRequest request, HttpServletResponse response, String errorCode) throws IOException {
        // Redirect back to frontend login with an error query parameter instead of breaking the filter chain
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/login").queryParam("error", errorCode).build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
