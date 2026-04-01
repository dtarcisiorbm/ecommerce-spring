package com.ecommerce_backend.backend.infrastructure.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletResponse response = attributes.getResponse();
        if (response == null) {
            return joinPoint.proceed();
        }

        String key = getKey(attributes.getRequest());
        Bucket bucket = cache.computeIfAbsent(key, k -> createBucket(rateLimit));

        if (bucket.tryConsume(1)) {
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            return joinPoint.proceed();
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "%s",
                    "timestamp": "%s"
                }
                """.formatted(
                    rateLimit.message(),
                    java.time.LocalDateTime.now()
                )
            );
            return null;
        }
    }

    private String getKey(jakarta.servlet.http.HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress + ":" + request.getRequestURI();
    }

    private Bucket createBucket(RateLimit rateLimit) {
        Bandwidth bandwidth = Bandwidth.classic(
            rateLimit.requests(),
            Refill.intervally(rateLimit.requests(), Duration.ofMinutes(rateLimit.windowMinutes()))
        );
        return Bucket.builder().addLimit(bandwidth).build();
    }
}
