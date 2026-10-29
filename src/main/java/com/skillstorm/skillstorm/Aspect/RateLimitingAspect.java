package com.skillstorm.skillstorm.Aspect;

import com.skillstorm.skillstorm.annotations.RateLimited;
import com.skillstorm.skillstorm.ratelimiting.ApiRateLimitingService;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Aspect
@RequiredArgsConstructor
public class RateLimitingAspect {

    private final ApiRateLimitingService rateLimitingService;

    @Around("@annotation(rateLimited)")
    public Object enforceRateLimited(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes == null) return joinPoint.proceed();

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        String clientIdentifier = getClientIdentifier(request);
        String methodSignature = joinPoint.getSignature().toShortString();
        String composedKey = buildKey(clientIdentifier,methodSignature);

        ConsumptionProbe probe = rateLimitingService.consume(composedKey,rateLimited);

        if(!probe.isConsumed()) {

            long retryAfterSeconds = Math.max(1,probe.getNanosToWaitForRefill() / 1_000_000_000L);
            String message = String.format("""
                        {
                            "status": 429,
                            "error": "Too Many Request",
                            "message": "Rate Limit exceeded. Try again in %d seconds"
                        }
                        """,retryAfterSeconds);

            if(response != null) {
                writeResponseHeader(response,retryAfterSeconds,message);
            }
            return null;
        }

        if(response != null) {
            response.setHeader("X-Rate-Limit-Remaining",String.valueOf(probe));
        }

        return joinPoint.proceed();
    }

    private String getClientIdentifier(HttpServletRequest request){
        String apiKey = request.getHeader("X-API-KEY");

        if(apiKey != null && !apiKey.isBlank()){
            return "apiKey:" + apiKey;
        }

        String xForwardFor = request.getHeader("X-Forwarded-For");
        if(xForwardFor != null && !xForwardFor.isBlank()){
            return "ip:" + xForwardFor.split(",")[0].trim();
        }

        return "ip:" + request.getRemoteAddr();
    }

    private String buildKey(String arg1,String arg2) {
        return arg1 + ":" + arg2;
    }

    private void writeResponseHeader(HttpServletResponse response,long retryAfterSeconds,String message) throws Throwable{
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setHeader("X-Rate-Limit-Remaining","0");
        response.setContentType("application/json");
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.getWriter().write(message);
        response.flushBuffer();
    }
}
