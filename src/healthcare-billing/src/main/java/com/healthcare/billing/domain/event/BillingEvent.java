package com.healthcare.billing.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public abstract class BillingEvent implements DomainEvent, Serializable {

    private final UUID eventId;
    private final Instant occurredAt;
    private final String triggeredBy;
    private final UUID aggregateId;

    protected BillingEvent(UUID aggregateId, String triggeredBy) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.triggeredBy = triggeredBy;
        this.aggregateId = aggregateId;
    }

    protected BillingEvent(String triggeredBy) {
        this(null, triggeredBy);
    }

    @Override
    public UUID eventId() { return eventId; }

    @Override
    public Instant occurredAt() { return occurredAt; }

    @Override
    public UUID aggregateId() { return aggregateId; }

    @Override
    public String eventType() { return getEventType(); }

    public UUID getEventId() { return eventId; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getTriggeredBy() { return triggeredBy; }

    public abstract String getEventType();
}
