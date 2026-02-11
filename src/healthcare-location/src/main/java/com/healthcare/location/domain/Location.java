package com.healthcare.location.domain;

import com.healthcare.common.domain.AggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "locations", indexes = {
    @Index(name = "idx_location_code", columnList = "location_code", unique = true),
    @Index(name = "idx_location_type", columnList = "location_type"),
    @Index(name = "idx_location_active", columnList = "is_active")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location extends AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "location_code", unique = true, nullable = false, length = 20)
    private String locationCode;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false, length = 30)
    private LocationType type;

    @Embedded
    private Address address;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "operating_hours", length = 500)
    private String operatingHours;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public static Location create(
            String locationCode,
            String name,
            LocationType type,
            Address address,
            String phoneNumber) {

        Objects.requireNonNull(locationCode, "Location code is required");
        Objects.requireNonNull(name, "Name is required");
        Objects.requireNonNull(type, "Type is required");
        Objects.requireNonNull(address, "Address is required");

        Location location = new Location();
        location.id = UUID.randomUUID();
        location.locationCode = locationCode;
        location.name = name;
        location.type = type;
        location.address = address;
        location.phoneNumber = phoneNumber;
        location.isActive = true;
        location.createdAt = Instant.now();
        location.updatedAt = Instant.now();

        return location;
    }

    public boolean canAcceptPatients() {
        return isActive && (capacity == null || capacity > 0);
    }

    public void setActive(boolean active) {
        this.isActive = active;
        this.updatedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
