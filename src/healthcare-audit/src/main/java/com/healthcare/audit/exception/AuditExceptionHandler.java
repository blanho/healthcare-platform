package com.healthcare.audit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice(basePackages = "com.healthcare.audit")
public class AuditExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AuditExceptionHandler.class);

    @ExceptionHandler(AuditEventNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleAuditEventNotFound(AuditEventNotFoundException ex) {
        log.warn("Audit event not found: {}", ex.getEventId());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Audit Event Not Found");
        problem.setType(URI.create("https://healthcare.com/problems/audit-event-not-found"));
        problem.setProperty("eventId", ex.getEventId());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(AuditLoggingException.class)
    public ResponseEntity<ProblemDetail> handleAuditLoggingError(AuditLoggingException ex) {
        log.error("Audit logging error: {}", ex.getMessage(), ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "Failed to log audit event");
        problem.setTitle("Audit Logging Error");
        problem.setType(URI.create("https://healthcare.com/problems/audit-logging-error"));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid audit query parameter: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid Parameter");
        problem.setType(URI.create("https://healthcare.com/problems/invalid-parameter"));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }
}
