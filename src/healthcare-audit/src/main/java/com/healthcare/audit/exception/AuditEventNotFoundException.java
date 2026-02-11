package com.healthcare.audit.exception;

import java.util.UUID;

public class AuditEventNotFoundException extends RuntimeException {

    private final UUID eventId;

    public AuditEventNotFoundException(UUID eventId) {
        super("Audit event not found: " + eventId);
        this.eventId = eventId;
    }

    public UUID getEventId() {
        return eventId;
    }
}
