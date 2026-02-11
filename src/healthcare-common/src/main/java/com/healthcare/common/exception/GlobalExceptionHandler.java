package com.healthcare.common.exception;

import com.healthcare.common.i18n.MessageService;
import jakarta.validation.ConstraintViolationException;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageService messageService;

    public GlobalExceptionHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("https://api.healthcare.com/errors/not-found"));
        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ProblemDetail handleBusinessRuleViolation(BusinessRuleViolationException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.getMessage()
        );
        problem.setTitle("Business Rule Violation");
        problem.setType(URI.create("https://api.healthcare.com/errors/business-rule-violation"));
        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ProblemDetail handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            ex.getMessage()
        );
        problem.setTitle("Access Denied");
        problem.setType(URI.create("https://api.healthcare.com/errors/access-denied"));
        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {

            String message = error.getDefaultMessage();
            fieldErrors.put(error.getField(), message);
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            messageService.getMessage("general.validation.failed")
        );
        problem.setTitle(messageService.getMessage("general.validation.failed"));
        problem.setType(URI.create("https://api.healthcare.com/errors/validation"));
        problem.setProperty("errorCode", "VALIDATION_ERROR");
        problem.setProperty("fieldErrors", fieldErrors);
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());

        Map<String, String> violations = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            violations.put(path, violation.getMessage());
        });

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Constraint validation failed"
        );
        problem.setTitle("Constraint Violation");
        problem.setType(URI.create("https://api.healthcare.com/errors/constraint-violation"));
        problem.setProperty("errorCode", "CONSTRAINT_VIOLATION");
        problem.setProperty("violations", violations);
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {

        log.error("Unexpected error occurred", ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            messageService.getMessage("error.internal")
        );
        problem.setTitle(messageService.getMessage("error.internal"));
        problem.setType(URI.create("https://api.healthcare.com/errors/internal-error"));
        problem.setProperty("errorCode", "INTERNAL_ERROR");
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }
}
