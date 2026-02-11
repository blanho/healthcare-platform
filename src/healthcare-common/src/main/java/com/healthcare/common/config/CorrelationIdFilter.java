package com.healthcare.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";

    public static final String MDC_CORRELATION_ID = "correlationId";
    public static final String MDC_REQUEST_ID = "requestId";
    public static final String MDC_USER_ID = "userId";
    public static final String MDC_REQUEST_METHOD = "requestMethod";
    public static final String MDC_REQUEST_URI = "requestUri";
    public static final String MDC_CLIENT_IP = "clientIp";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {

            String correlationId = getCorrelationId(request);

            MDC.put(MDC_CORRELATION_ID, correlationId);
            MDC.put(MDC_REQUEST_ID, correlationId);
            MDC.put(MDC_REQUEST_METHOD, request.getMethod());
            MDC.put(MDC_REQUEST_URI, request.getRequestURI());
            MDC.put(MDC_CLIENT_IP, getClientIpAddress(request));

            setUserIdFromSecurityContext();

            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            response.setHeader(REQUEST_ID_HEADER, correlationId);

            filterChain.doFilter(request, response);

        } finally {

            MDC.clear();
        }
    }

    private String getCorrelationId(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(CORRELATION_ID_HEADER))
            .filter(id -> !id.isBlank())
            .orElseGet(() -> Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
                .filter(id -> !id.isBlank())
                .orElseGet(() -> UUID.randomUUID().toString()));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {

            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private void setUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            MDC.put(MDC_USER_ID, authentication.getName());
        } else {
            MDC.put(MDC_USER_ID, "anonymous");
        }
    }

    public static void setUserId(String userId) {
        if (userId != null && !userId.isBlank()) {
            MDC.put(MDC_USER_ID, userId);
        }
    }

    public static String getCurrentCorrelationId() {
        return MDC.get(MDC_CORRELATION_ID);
    }
}
