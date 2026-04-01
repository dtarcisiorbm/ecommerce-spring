package com.ecommerce_backend.backend.infrastructure.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // Diferentes limites para diferentes tipos de endpoints
    private static final Bandwidth AUTH_BANDWIDTH = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
    private static final Bandwidth GENERAL_BANDWIDTH = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
    private static final Bandwidth ADMIN_BANDWIDTH = Bandwidth.classic(200, Refill.intervally(200, Duration.ofMinutes(1)));

    public Bucket resolveBucket(HttpServletRequest request) {
        String key = getKey(request);
        return cache.computeIfAbsent(key, k -> newBucket(getBandwidthForEndpoint(request)));
    }

    private String getKey(HttpServletRequest request) {
        // Usa IP como chave para rate limiting
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        
        // Combina IP com endpoint para limites específicos
        return ipAddress + ":" + request.getRequestURI();
    }

    private Bandwidth getBandwidthForEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Endpoints de autenticação têm limites mais restritos
        if (path.contains("/auth/")) {
            return AUTH_BANDWIDTH;
        }
        
        // Endpoints admin têm limites mais altos
        if (path.contains("/users/") || path.contains("/admin/")) {
            return ADMIN_BANDWIDTH;
        }
        
        // Demais endpoints usam limite geral
        return GENERAL_BANDWIDTH;
    }

    private Bucket newBucket(Bandwidth bandwidth) {
        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }
}
