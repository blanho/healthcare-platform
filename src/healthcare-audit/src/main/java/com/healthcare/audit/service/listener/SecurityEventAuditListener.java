package com.healthcare.audit.service.listener;

import com.healthcare.audit.domain.AuditAction;
import com.healthcare.audit.domain.AuditOutcome;
import com.healthcare.audit.service.AuditService;
import com.healthcare.auth.domain.event.UserLockedEvent;
import com.healthcare.auth.domain.event.UserLoggedInEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SecurityEventAuditListener {

    private static final Logger log = LoggerFactory.getLogger(SecurityEventAuditListener.class);

    private final AuditService auditService;

    public SecurityEventAuditListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserLoggedIn(UserLoggedInEvent event) {
        log.info("Auditing UserLoggedInEvent: userId={}, username={}",
            event.aggregateId(), event.username());

        try {
            auditService.logAuthentication(
                event.aggregateId(),
                event.username(),
                AuditAction.LOGIN,
                AuditOutcome.SUCCESS,
                hashIpAddress(event.ipAddress())
            );

            log.debug("Recorded login audit for user: {}", event.username());
        } catch (Exception e) {
            log.error("Failed to record login audit for user: {}", event.username(), e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserLocked(UserLockedEvent event) {
        log.warn("Auditing UserLockedEvent: userId={}, username={}, failedAttempts={}",
            event.aggregateId(), event.username(), event.failedAttempts());

        try {
            auditService.logAuthentication(
                event.aggregateId(),
                event.username(),
                AuditAction.LOGIN_FAILED,
                AuditOutcome.FAILURE,
                null
            );

            log.info("Recorded account lockout audit for user: {}", event.username());
        } catch (Exception e) {
            log.error("Failed to record account lockout audit for user: {}", event.username(), e);
        }
    }

    private String hashIpAddress(String ipAddress) {
        if (ipAddress == null) {
            return null;
        }

        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ipAddress.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            log.error("Failed to hash IP address", e);
            return null;
        }
    }
}
