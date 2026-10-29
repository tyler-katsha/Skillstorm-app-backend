package com.skillstorm.skillstorm.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String error = "oauth_failed";

        String link = String.format("http://localhost:5173/login?error=%s",error);

        getRedirectStrategy().sendRedirect(request,response,link);
    }
}
