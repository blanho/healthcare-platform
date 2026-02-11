package com.healthcare.audit.aspect;

import com.healthcare.audit.domain.*;
import com.healthcare.audit.service.AuditContextProvider;
import com.healthcare.audit.service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Aspect
@Component
@Order(1)
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private final AuditService auditService;
    private final AuditContextProvider contextProvider;

    public AuditAspect(AuditService auditService, AuditContextProvider contextProvider) {
        this.auditService = auditService;
        this.contextProvider = contextProvider;
    }

    @Pointcut("@annotation(com.healthcare.audit.domain.Audited)")
    public void auditedMethod() {}

    @Pointcut("@within(com.healthcare.audit.domain.Audited)")
    public void auditedClass() {}

    @Pointcut("execution(* org.springframework.data.repository.CrudRepository+.save(..))")
    public void repositorySave() {}

    @Pointcut("execution(* org.springframework.data.repository.CrudRepository+.delete*(..))")
    public void repositoryDelete() {}

    @Around("auditedMethod()")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Audited audited = getAuditedAnnotation(joinPoint);

        AuditEvent.Builder eventBuilder = contextProvider.createEventBuilder()
            .action(audited.action())
            .resourceCategory(audited.category())
            .description(audited.description().isEmpty() ?
                getDefaultDescription(joinPoint) : audited.description());

        UUID resourceId = extractResourceId(joinPoint);
        if (resourceId != null) {
            eventBuilder.resourceId(resourceId);
        }

        UUID patientId = extractPatientId(joinPoint);
        if (patientId != null) {
            eventBuilder.patientId(patientId);
        }

        try {
            Object result = joinPoint.proceed();

            long responseTime = System.currentTimeMillis() - startTime;
            eventBuilder
                .outcome(AuditOutcome.SUCCESS)
                .responseTimeMs(responseTime);

            if (result != null && resourceId == null) {
                UUID resultId = extractIdFromResult(result);
                if (resultId != null) {
                    eventBuilder.resourceId(resultId);
                }
            }

            auditService.logEvent(eventBuilder);
            return result;

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            eventBuilder
                .outcome(AuditOutcome.ERROR)
                .responseTimeMs(responseTime)
                .errorCode(e.getClass().getSimpleName())
                .errorMessage(sanitizeErrorMessage(e.getMessage()));

            auditService.logEvent(eventBuilder);
            throw e;
        }
    }

    @AfterReturning(pointcut = "auditedClass() && execution(* *(..))", returning = "result")
    public void auditClassMethodSuccess(JoinPoint joinPoint, Object result) {

        logClassMethodExecution(joinPoint, result, null);
    }

    @AfterThrowing(pointcut = "auditedClass() && execution(* *(..))", throwing = "exception")
    public void auditClassMethodFailure(JoinPoint joinPoint, Throwable exception) {

        logClassMethodExecution(joinPoint, null, exception);
    }

    private Audited getAuditedAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Audited audited = method.getAnnotation(Audited.class);
        if (audited == null) {

            audited = method.getDeclaringClass().getAnnotation(Audited.class);
        }

        return audited;
    }

    private String getDefaultDescription(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        return className + "." + methodName;
    }

    private UUID extractResourceId(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return null;
        }

        for (Object arg : args) {
            if (arg instanceof UUID) {
                return (UUID) arg;
            }
        }

        for (Object arg : args) {
            if (arg != null) {
                UUID id = extractIdFromResult(arg);
                if (id != null) {
                    return id;
                }
            }
        }

        return null;
    }

    private UUID extractPatientId(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                if ("patientId".equalsIgnoreCase(paramNames[i]) && args[i] instanceof UUID) {
                    return (UUID) args[i];
                }
            }
        }

        return null;
    }

    private UUID extractIdFromResult(Object result) {
        if (result == null) {
            return null;
        }

        try {
            Method getIdMethod = result.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(result);
            if (id instanceof UUID) {
                return (UUID) id;
            }
        } catch (Exception e) {

        }

        return null;
    }

    private void logClassMethodExecution(JoinPoint joinPoint, Object result, Throwable exception) {
        try {
            Audited audited = joinPoint.getTarget().getClass().getAnnotation(Audited.class);
            if (audited == null) {
                return;
            }

            AuditAction action = determineAction(joinPoint.getSignature().getName());
            AuditOutcome outcome = exception == null ? AuditOutcome.SUCCESS : AuditOutcome.ERROR;

            AuditEvent.Builder eventBuilder = contextProvider.createEventBuilder()
                .action(action)
                .outcome(outcome)
                .resourceCategory(audited.category())
                .description(getDefaultDescription(joinPoint));

            UUID resourceId = extractResourceId(joinPoint);
            if (resourceId == null && result != null) {
                resourceId = extractIdFromResult(result);
            }
            if (resourceId != null) {
                eventBuilder.resourceId(resourceId);
            }

            if (exception != null) {
                eventBuilder
                    .errorCode(exception.getClass().getSimpleName())
                    .errorMessage(sanitizeErrorMessage(exception.getMessage()));
            }

            auditService.logEvent(eventBuilder);

        } catch (Exception e) {
            log.warn("Failed to log audit event for class method: {}", e.getMessage());
        }
    }

    private AuditAction determineAction(String methodName) {
        String lowerName = methodName.toLowerCase();

        if (lowerName.startsWith("get") || lowerName.startsWith("find") ||
            lowerName.startsWith("read") || lowerName.startsWith("fetch")) {
            return AuditAction.READ;
        }
        if (lowerName.startsWith("create") || lowerName.startsWith("add") ||
            lowerName.startsWith("insert") || lowerName.startsWith("save")) {
            return AuditAction.CREATE;
        }
        if (lowerName.startsWith("update") || lowerName.startsWith("modify") ||
            lowerName.startsWith("edit") || lowerName.startsWith("set")) {
            return AuditAction.UPDATE;
        }
        if (lowerName.startsWith("delete") || lowerName.startsWith("remove")) {
            return AuditAction.DELETE;
        }
        if (lowerName.startsWith("export") || lowerName.startsWith("download")) {
            return AuditAction.EXPORT;
        }
        if (lowerName.startsWith("search") || lowerName.startsWith("query")) {
            return AuditAction.SEARCH;
        }

        return AuditAction.READ;
    }

    private String sanitizeErrorMessage(String message) {
        if (message == null) {
            return null;
        }

        String sanitized = message;

        sanitized = sanitized.replaceAll("\\d{3}-\\d{2}-\\d{4}", "***-**-****");
        sanitized = sanitized.replaceAll("\\d{3}-\\d{3}-\\d{4}", "***-***-****");
        sanitized = sanitized.replaceAll("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", "***@***.***");

        if (sanitized.length() > 500) {
            sanitized = sanitized.substring(0, 497) + "...";
        }

        return sanitized;
    }
}
