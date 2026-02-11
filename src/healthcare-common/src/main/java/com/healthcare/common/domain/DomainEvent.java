package com.healthcare.common.domain;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID eventId();

    Instant occurredAt();

    UUID aggregateId();

    default String eventType() {
        return this.getClass().getSimpleName();
    }
}
