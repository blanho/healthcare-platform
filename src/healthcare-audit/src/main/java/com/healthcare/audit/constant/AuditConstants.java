package com.healthcare.audit.constant;

public final class AuditConstants {

    private AuditConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    public static final int AUDIT_RETENTION_YEARS = 6;
    public static final int AUDIT_RETENTION_DAYS = AUDIT_RETENTION_YEARS * 365;

    public static final String CATEGORY_PHI_ACCESS = "PHI_ACCESS";
    public static final String CATEGORY_AUTHENTICATION = "AUTHENTICATION";
    public static final String CATEGORY_AUTHORIZATION = "AUTHORIZATION";
    public static final String CATEGORY_DATA_MODIFICATION = "DATA_MODIFICATION";
    public static final String CATEGORY_DATA_EXPORT = "DATA_EXPORT";

    public static final String SEVERITY_LOW = "LOW";
    public static final String SEVERITY_MEDIUM = "MEDIUM";
    public static final String SEVERITY_HIGH = "HIGH";
    public static final String SEVERITY_CRITICAL = "CRITICAL";
}
