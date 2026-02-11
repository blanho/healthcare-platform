package com.healthcare.provider.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ProviderActivatedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID providerId,
    String providerNumber
) implements DomainEvent {

    public ProviderActivatedEvent(UUID providerId, String providerNumber) {
        this(UUID.randomUUID(), Instant.now(), providerId, providerNumber);
    }

    @Override
    public UUID aggregateId() {
        return providerId;
    }

    @Override
    public String eventType() {
        return "provider.activated";
    }
}
