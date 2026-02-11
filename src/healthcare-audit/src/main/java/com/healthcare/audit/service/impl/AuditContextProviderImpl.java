package com.healthcare.audit.service.impl;

import com.healthcare.audit.domain.*;
import com.healthcare.audit.service.AuditContextProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
public class AuditContextProviderImpl implements AuditContextProvider {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";
    private static final String UNKNOWN_USER = "UNKNOWN";

    @Override
    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof String) {
            try {
                return UUID.fromString((String) principal);
            } catch (IllegalArgumentException e) {

            }
        }

        String name = auth.getName();
        if (name != null && !name.equals("anonymousUser")) {
            try {
                return UUID.fromString(name);
            } catch (IllegalArgumentException e) {

                return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
            }
        }

        return null;
    }

    @Override
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return UNKNOWN_USER;
        }
        return auth.getName();
    }

    @Override
    public String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        return auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(a -> a.startsWith("ROLE_"))
            .findFirst()
            .map(a -> a.substring(5))
            .orElse(null);
    }

    @Override
    public String getCorrelationId() {

        String correlationId = MDC.get(CORRELATION_ID_MDC_KEY);
        if (correlationId != null) {
            return correlationId;
        }

        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            correlationId = request.getHeader(CORRELATION_ID_HEADER);
            if (correlationId != null) {
                return correlationId;
            }
        }

        return UUID.randomUUID().toString();
    }

    @Override
    public String getSessionId() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null && request.getSession(false) != null) {
            return request.getSession().getId();
        }
        return null;
    }

    @Override
    public String hashClientIp(String clientIp) {
        if (clientIp == null) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(clientIp.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return "HASH_ERROR";
        }
    }

    @Override
    public String getClientIpHash() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        String clientIp = getClientIpFromRequest(request);
        return hashClientIp(clientIp);
    }

    @Override
    public String getUserAgent() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String userAgent = request.getHeader("User-Agent");

            if (userAgent != null && userAgent.length() > 255) {
                return userAgent.substring(0, 255);
            }
            return userAgent;
        }
        return null;
    }

    @Override
    public String getRequestUri() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getRequestURI();
        }
        return null;
    }

    @Override
    public String getHttpMethod() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getMethod();
        }
        return null;
    }

    @Override
    public AuditEvent.Builder createEventBuilder() {
        return AuditEvent.builder()
            .userId(getCurrentUserId())
            .username(getCurrentUsername())
            .userRole(getCurrentUserRole())
            .correlationId(getCorrelationId())
            .sessionId(getSessionId())
            .clientIpHash(getClientIpHash())
            .userAgent(getUserAgent())
            .httpMethod(getHttpMethod())
            .requestUri(getRequestUri());
    }

    @Override
    public AuditEvent.Builder createEventBuilder(AuditAction action, AuditOutcome outcome,
                                                 ResourceCategory category, UUID resourceId) {
        return createEventBuilder()
            .action(action)
            .outcome(outcome)
            .resourceCategory(category)
            .resourceId(resourceId);
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attrs.getRequest();
        } catch (IllegalStateException e) {

            return null;
        }
    }

    private String getClientIpFromRequest(HttpServletRequest request) {

        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {

                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }
}
