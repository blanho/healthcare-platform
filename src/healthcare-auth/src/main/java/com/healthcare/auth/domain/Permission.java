package com.healthcare.auth.domain;

import com.healthcare.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions",
    indexes = {
        @Index(name = "idx_permission_resource", columnList = "resource")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_permission_resource_action", columnNames = {"resource", "action"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Permission extends BaseEntity {

    public static final String PATIENT_READ = "patient:read";
    public static final String PATIENT_WRITE = "patient:write";
    public static final String PATIENT_DELETE = "patient:delete";

    public static final String MEDICAL_RECORD_READ = "medical_record:read";
    public static final String MEDICAL_RECORD_WRITE = "medical_record:write";

    public static final String APPOINTMENT_READ = "appointment:read";
    public static final String APPOINTMENT_WRITE = "appointment:write";
    public static final String APPOINTMENT_DELETE = "appointment:delete";

    public static final String BILLING_READ = "billing:read";
    public static final String BILLING_WRITE = "billing:write";

    public static final String USER_READ = "user:read";
    public static final String USER_WRITE = "user:write";

    public static final String AUDIT_READ = "audit:read";

    public static final String PROVIDER_READ = "provider:read";
    public static final String PROVIDER_WRITE = "provider:write";

    public static final String PRESCRIPTION_READ = "prescription:read";
    public static final String PRESCRIPTION_WRITE = "prescription:write";

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "resource", nullable = false, length = 100)
    private String resource;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "description", length = 255)
    private String description;

    public Permission(String name, String resource, String action, String description) {
        this.name = name;
        this.resource = resource;
        this.action = action;
        this.description = description;
    }

    public static Permission of(String resourceAction, String description) {
        String[] parts = resourceAction.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Permission must be in format resource:action");
        }
        return new Permission(resourceAction, parts[0], parts[1], description);
    }

    public boolean isForResource(String resource) {
        return this.resource.equals(resource);
    }

    public boolean isReadPermission() {
        return "read".equals(action);
    }

    public boolean isWritePermission() {
        return "write".equals(action);
    }

    public boolean isDeletePermission() {
        return "delete".equals(action);
    }
}
