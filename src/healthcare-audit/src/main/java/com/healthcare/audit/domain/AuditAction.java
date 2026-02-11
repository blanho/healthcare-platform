package com.healthcare.audit.domain;

public enum AuditAction {

    CREATE("CREATE", "Resource created"),
    READ("READ", "Resource read/viewed"),
    UPDATE("UPDATE", "Resource updated"),
    DELETE("DELETE", "Resource deleted"),

    VIEW("VIEW", "Resource viewed"),
    SEARCH("SEARCH", "Search performed"),
    EXPORT("EXPORT", "Data exported"),
    PRINT("PRINT", "Data printed"),
    DOWNLOAD("DOWNLOAD", "Data downloaded"),

    LOGIN("LOGIN", "User logged in"),
    LOGOUT("LOGOUT", "User logged out"),
    LOGIN_FAILED("LOGIN_FAILED", "Login attempt failed"),
    PASSWORD_CHANGE("PASSWORD_CHANGE", "Password changed"),
    PASSWORD_RESET("PASSWORD_RESET", "Password reset requested"),

    ACCESS_DENIED("ACCESS_DENIED", "Access denied"),
    PERMISSION_GRANTED("PERMISSION_GRANTED", "Permission granted"),
    PERMISSION_REVOKED("PERMISSION_REVOKED", "Permission revoked"),
    ROLE_ASSIGNED("ROLE_ASSIGNED", "Role assigned to user"),
    ROLE_REVOKED("ROLE_REVOKED", "Role revoked from user"),

    PHI_ACCESS("PHI_ACCESS", "Protected health information accessed"),
    PHI_DISCLOSURE("PHI_DISCLOSURE", "PHI disclosed to third party"),
    PHI_AMENDMENT("PHI_AMENDMENT", "PHI amendment requested"),
    PHI_RESTRICTION("PHI_RESTRICTION", "PHI access restriction set"),

    SYSTEM_CONFIG("SYSTEM_CONFIG", "System configuration changed"),
    AUDIT_CONFIG("AUDIT_CONFIG", "Audit configuration changed"),
    DATA_IMPORT("DATA_IMPORT", "Data imported"),
    DATA_ARCHIVE("DATA_ARCHIVE", "Data archived"),
    DATA_PURGE("DATA_PURGE", "Data purged");

    private final String code;
    private final String description;

    AuditAction(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public boolean isPhiRelated() {
        return this == PHI_ACCESS || this == PHI_DISCLOSURE ||
               this == PHI_AMENDMENT || this == PHI_RESTRICTION ||
               this == READ || this == VIEW || this == EXPORT ||
               this == PRINT || this == DOWNLOAD;
    }

    public boolean isSecurityRelated() {
        return this == LOGIN || this == LOGOUT || this == LOGIN_FAILED ||
               this == PASSWORD_CHANGE || this == PASSWORD_RESET ||
               this == ACCESS_DENIED || this == PERMISSION_GRANTED ||
               this == PERMISSION_REVOKED || this == ROLE_ASSIGNED ||
               this == ROLE_REVOKED;
    }
}
