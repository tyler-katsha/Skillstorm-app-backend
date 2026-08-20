package com.skillstorm.skillstorm.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class JwtHelper {
    public static String extractToken(HttpServletRequest request){


        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ") && !bearerToken.equals("Bearer null")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
