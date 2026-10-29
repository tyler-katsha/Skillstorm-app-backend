package com.skillstorm.skillstorm.ratelimiting;

import com.skillstorm.skillstorm.utils.BotFingerprintSignatures;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@RequiredArgsConstructor
@Slf4j
public class TlsFingerprintFilter extends OncePerRequestFilter {

    private final static String JA3_HEADER = "X-TLS-JA3";

    private final FingerprintRateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull  FilterChain filterChain) throws ServletException, IOException {
        var ja3 = request.getHeader(JA3_HEADER);
        var userAgent = request.getHeader("User-Agent");

        var remoteAddr = request.getRemoteAddr();

        if (remoteAddr.startsWith("10.") || remoteAddr.startsWith("127.") || "0:0:0:0:0:0:0:1".equals(remoteAddr)) {
            filterChain.doFilter(request, response);
            return;
        }

        if(ja3 == null || ja3.isBlank()){
            log.warn("Missing {} header. Direct connection attempt from IP. {}",JA3_HEADER,request.getRemoteAddr());
            filterChain.doFilter(request,response);
            return;
        }

        if(BotFingerprintSignatures.KNOWN_BOT_JA3.contains(ja3)){
            log.warn("Blocked request with blacklisted bot JA3: {} (UA: {})", ja3, userAgent);
            writeErrorResponse(response, HttpStatus.FORBIDDEN, "Access denied: automated client detected");
            return;
        }

        if(BotFingerprintSignatures.CHROME_FAMILY_JA3.contains(ja3)){
            log.warn("Blocked request with blacklisted bot Chrome JA3: {} (UA: {})", ja3, userAgent);
            writeErrorResponse(response, HttpStatus.FORBIDDEN, "Access denied: automated client detected");
            return;
        }

        if (userAgent != null && BotFingerprintSignatures.BROWSER_UA_PATTERN.matcher(userAgent).matches()) {
            if (BotFingerprintSignatures.KNOWN_BOT_JA3.contains(ja3)) {
                log.warn("UA Spoofing detected! UA: '{}' accompanied by Bot JA3: {}", userAgent, ja3);
                writeErrorResponse(response, HttpStatus.FORBIDDEN, "Client handshake inconsistency");
                return;
            }
        }

        if (!rateLimiter.isAllowed(ja3)) {
            log.warn("Rate limit exceeded for JA3 fingerprint: {} (IP: {})", ja3, request.getRemoteAddr());
            writeErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded. Please slow down.");
            return;
        }

        request.setAttribute("CLIENT_TLS_JA3",ja3);

        filterChain.doFilter(request,response);
    }
    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }
}
