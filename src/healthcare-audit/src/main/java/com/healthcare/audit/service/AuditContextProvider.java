package com.healthcare.audit.service;

import com.healthcare.audit.domain.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface AuditContextProvider {

    UUID getCurrentUserId();

    String getCurrentUsername();

    String getCurrentUserRole();

    String getCorrelationId();

    String getSessionId();

    String hashClientIp(String clientIp);

    String getClientIpHash();

    String getUserAgent();

    String getRequestUri();

    String getHttpMethod();

    AuditEvent.Builder createEventBuilder();

    AuditEvent.Builder createEventBuilder(AuditAction action, AuditOutcome outcome,
                                          ResourceCategory category, UUID resourceId);
}
