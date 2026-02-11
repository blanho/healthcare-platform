package com.healthcare.audit.aspect;

import com.healthcare.audit.domain.*;
import com.healthcare.audit.service.AuditContextProvider;
import com.healthcare.audit.service.AuditService;
import com.healthcare.audit.service.DataMaskingService;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Component
public class AuditEntityListener {

    private static final Logger log = LoggerFactory.getLogger(AuditEntityListener.class);

    private static AuditService auditService;
    private static AuditContextProvider contextProvider;
    private static DataMaskingService dataMaskingService;

    @Autowired
    public void init(AuditService auditService,
                     AuditContextProvider contextProvider,
                     DataMaskingService dataMaskingService) {
        AuditEntityListener.auditService = auditService;
        AuditEntityListener.contextProvider = contextProvider;
        AuditEntityListener.dataMaskingService = dataMaskingService;
    }

    @PostLoad
    public void onPostLoad(Object entity) {
        if (!isAuditableEntity(entity)) {
            return;
        }

        try {
            Audited audited = entity.getClass().getAnnotation(Audited.class);
            logEntityEvent(entity, AuditAction.READ, audited);
        } catch (Exception e) {
            log.warn("Failed to log entity read: {}", e.getMessage());
        }
    }

    @PostPersist
    public void onPostPersist(Object entity) {
        if (!isAuditableEntity(entity)) {
            return;
        }

        try {
            Audited audited = entity.getClass().getAnnotation(Audited.class);
            logEntityEvent(entity, AuditAction.CREATE, audited);
        } catch (Exception e) {
            log.warn("Failed to log entity create: {}", e.getMessage());
        }
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        if (!isAuditableEntity(entity)) {
            return;
        }

        try {
            Audited audited = entity.getClass().getAnnotation(Audited.class);
            logEntityEvent(entity, AuditAction.UPDATE, audited);
        } catch (Exception e) {
            log.warn("Failed to log entity update: {}", e.getMessage());
        }
    }

    @PreRemove
    public void onPreRemove(Object entity) {
        if (!isAuditableEntity(entity)) {
            return;
        }

        try {
            Audited audited = entity.getClass().getAnnotation(Audited.class);
            logEntityEvent(entity, AuditAction.DELETE, audited);
        } catch (Exception e) {
            log.warn("Failed to log entity delete: {}", e.getMessage());
        }
    }

    private boolean isAuditableEntity(Object entity) {
        if (entity == null) {
            return false;
        }

        return entity.getClass().isAnnotationPresent(Audited.class);
    }

    private void logEntityEvent(Object entity, AuditAction action, Audited audited) {
        if (auditService == null || contextProvider == null) {
            log.warn("Audit services not initialized, skipping audit log");
            return;
        }

        UUID entityId = extractEntityId(entity);
        UUID patientId = extractPatientId(entity);
        ResourceCategory category = audited != null ?
            audited.category() : ResourceCategory.PATIENT;

        String description = String.format("%s %s [%s]",
            action.getDescription(),
            entity.getClass().getSimpleName(),
            entityId != null ? entityId.toString() : "unknown");

        AuditEvent.Builder eventBuilder = contextProvider.createEventBuilder()
            .action(action)
            .outcome(AuditOutcome.SUCCESS)
            .resourceCategory(category)
            .resourceId(entityId)
            .resourceType(entity.getClass().getName())
            .patientId(patientId)
            .description(description);

        auditService.logEvent(eventBuilder);
    }

    private UUID extractEntityId(Object entity) {
        try {
            Method getIdMethod = entity.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(entity);
            if (id instanceof UUID) {
                return (UUID) id;
            }
        } catch (Exception e) {

        }
        return null;
    }

    private UUID extractPatientId(Object entity) {

        String[] patientIdMethods = {"getPatientId", "getPatient"};

        for (String methodName : patientIdMethods) {
            try {
                Method method = entity.getClass().getMethod(methodName);
                Object result = method.invoke(entity);

                if (result instanceof UUID) {
                    return (UUID) result;
                }

                if (result != null) {
                    return extractEntityId(result);
                }
            } catch (Exception e) {

            }
        }

        return null;
    }
}
