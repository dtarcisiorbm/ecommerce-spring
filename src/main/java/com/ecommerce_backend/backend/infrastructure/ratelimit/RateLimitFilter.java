package com.ecommerce_backend.backend.infrastructure.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Ignora rate limiting para endpoints de documentação e health checks
        if (isExcludedPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        Bucket bucket = rateLimitService.resolveBucket(request);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Adiciona headers informativos sobre o rate limiting
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            response.setHeader("X-Rate-Limit-Limit", "100"); // Valor padrão, pode ser dinâmico
            filterChain.doFilter(request, response);
        } else {
            // Rate limit excedido
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            
            response.setHeader("X-Rate-Limit-Retry-After", String.valueOf(waitForRefill));
            response.setHeader("X-Rate-Limit-Remaining", "0");
            response.setHeader("X-Rate-Limit-Limit", "100");
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "Rate limit exceeded. Please try again later.",
                    "details": "You have exceeded the rate limit for this endpoint. Try again in %d seconds.",
                    "retryAfter": %d,
                    "timestamp": "%s",
                    "path": "%s"
                }
                """.formatted(
                    waitForRefill,
                    waitForRefill,
                    java.time.LocalDateTime.now(),
                    request.getRequestURI()
                )
            );
        }
    }

    private boolean isExcludedPath(String path) {
        return path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/actuator") ||
               path.equals("/health");
    }
}
