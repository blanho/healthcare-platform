package com.healthcare.audit.service;

import com.healthcare.audit.api.dto.AuditEventResponse;
import java.util.List;

public interface AuditQueryService {

    List<AuditEventResponse> getRecentEvents(int limit);
}
