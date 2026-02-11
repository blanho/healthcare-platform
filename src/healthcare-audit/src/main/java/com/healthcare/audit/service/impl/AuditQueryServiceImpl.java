package com.healthcare.audit.service.impl;

import com.healthcare.audit.api.dto.AuditEventResponse;
import com.healthcare.audit.domain.AuditEvent;
import com.healthcare.audit.repository.AuditEventRepository;
import com.healthcare.audit.service.AuditQueryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuditQueryServiceImpl implements AuditQueryService {

    private final AuditEventRepository auditEventRepository;

    public AuditQueryServiceImpl(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Override
    public List<AuditEventResponse> getRecentEvents(int limit) {
        var pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "eventTimestamp"));
        return auditEventRepository.findAll(pageable)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private AuditEventResponse toResponse(AuditEvent event) {
        return new AuditEventResponse(
            event.getId(),
            event.getEventTimestamp(),
            event.getCorrelationId(),
            event.getSessionId(),
            event.getUserId(),
            event.getUsername(),
            event.getUserRole(),
            event.getAction(),
            event.getOutcome(),
            event.getSeverity(),
            event.getDescription(),
            event.getResourceCategory(),
            event.getResourceId(),
            event.getResourceType(),
            event.getPatientId(),
            event.getAccessedFields(),
            event.getChangedFields(),
            event.getHttpMethod(),
            event.getRequestUri(),
            event.getResponseStatus(),
            event.getResponseTimeMs(),
            event.getErrorCode(),
            event.getErrorMessage(),
            event.isPhiAccess(),
            event.isSecurityConcern()
        );
    }
}
