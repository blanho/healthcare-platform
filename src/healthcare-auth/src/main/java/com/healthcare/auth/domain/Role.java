package com.healthcare.auth.domain;

import com.healthcare.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_role_name", columnList = "name")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseEntity {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String DOCTOR = "ROLE_DOCTOR";
    public static final String NURSE = "ROLE_NURSE";
    public static final String RECEPTIONIST = "ROLE_RECEPTIONIST";
    public static final String BILLING = "ROLE_BILLING";
    public static final String PATIENT = "ROLE_PATIENT";
    public static final String LAB_TECH = "ROLE_LAB_TECH";
    public static final String PHARMACIST = "ROLE_PHARMACIST";

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_system_role")
    private boolean systemRole = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @BatchSize(size = 25)
    private Set<Permission> permissions = new HashSet<>();

    public Role(String name, String description, boolean systemRole) {
        this.name = name;
        this.description = description;
        this.systemRole = systemRole;
    }

    public Role(String name, String description) {
        this(name, description, false);
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public boolean hasPermission(String permissionName) {
        return permissions.stream()
            .anyMatch(p -> p.getName().equals(permissionName));
    }

    public boolean hasPermission(String resource, String action) {
        return permissions.stream()
            .anyMatch(p -> p.getResource().equals(resource) && p.getAction().equals(action));
    }

    public Set<String> getPermissionNames() {
        Set<String> names = new HashSet<>();
        for (Permission permission : permissions) {
            names.add(permission.getName());
        }
        return names;
    }

    public boolean isProviderRole() {
        return name.equals(DOCTOR) || name.equals(NURSE) ||
               name.equals(LAB_TECH) || name.equals(PHARMACIST);
    }

    public boolean isAdminRole() {
        return name.equals(ADMIN);
    }
}
