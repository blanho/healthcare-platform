package com.healthcare.common.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(2)
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Value("${healthcare.ratelimit.anonymous.requests:30}")
    private int anonymousRequestsPerMinute;

    @Value("${healthcare.ratelimit.authenticated.requests:100}")
    private int authenticatedRequestsPerMinute;

    @Value("${healthcare.ratelimit.admin.requests:500}")
    private int adminRequestsPerMinute;

    @Value("${healthcare.ratelimit.enabled:true}")
    private boolean rateLimitEnabled;

    private static final String[] EXCLUDED_PATHS = {
        "/actuator/health",
        "/actuator/info",
        "/actuator/prometheus",
        "/swagger-ui",
        "/v3/api-docs"
    };

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        for (String excluded : EXCLUDED_PATHS) {
            if (path.startsWith(excluded)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String clientKey = resolveClientKey(request);
        Bucket bucket = resolveBucket(clientKey, request);

        if (bucket.tryConsume(1)) {

            filterChain.doFilter(request, response);
        } else {

            log.warn("Rate limit exceeded for client: {}, path: {}", clientKey, path);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/problem+json");
            response.setHeader("Retry-After", "60");
            response.setHeader("X-RateLimit-Remaining", "0");

            String errorResponse = """
                {
                    "type": "https:
                    "title": "Too Many Requests",
                    "status": 429,
                    "detail": "Rate limit exceeded. Please retry after 60 seconds.",
                    "instance": "%s"
                }
                """.formatted(path);

            response.getWriter().write(errorResponse);
        }
    }

    /**
     * Resolves a unique key for the client.
     * Uses authenticated username if available, otherwise IP address.
     */
    private String resolveClientKey(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            return "user:" + auth.getName();
        }

        // Fall back to IP address for anonymous users
        return "ip:" + getClientIp(request);
    }

    /**
     * Gets or creates a rate limit bucket for the client.
     */
    private Bucket resolveBucket(String key, HttpServletRequest request) {
        return buckets.computeIfAbsent(key, k -> createBucket(request));
    }

    /**
     * Creates a new bucket with appropriate limits based on authentication.
     */
    private Bucket createBucket(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        int limit;
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            limit = anonymousRequestsPerMinute;
        } else if (hasAdminRole(auth)) {
            limit = adminRequestsPerMinute;
        } else {
            limit = authenticatedRequestsPerMinute;
        }

        return Bucket.builder()
            .addLimit(Bandwidth.classic(limit, Refill.greedy(limit, Duration.ofMinutes(1))))
            .build();
    }

    /**
     * Checks if the authentication has admin role.
     */
    private boolean hasAdminRole(Authentication auth) {
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Gets client IP, respecting X-Forwarded-For for proxied requests.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Clears the bucket cache. Useful for testing.
     */
    public void clearBuckets() {
        buckets.clear();
    }
}
