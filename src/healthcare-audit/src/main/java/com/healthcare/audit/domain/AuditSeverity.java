package com.healthcare.audit.domain;

public enum AuditSeverity {
    LOW("LOW", 1, "Informational event"),
    MEDIUM("MEDIUM", 2, "Notable event requiring attention"),
    HIGH("HIGH", 3, "Important event for review"),
    CRITICAL("CRITICAL", 4, "Critical event requiring immediate action");

    private final String code;
    private final int level;
    private final String description;

    AuditSeverity(String code, int level, String description) {
        this.code = code;
        this.level = level;
        this.description = description;
    }

    public String getCode() { return code; }
    public int getLevel() { return level; }
    public String getDescription() { return description; }

    public static AuditSeverity forActionAndOutcome(AuditAction action, AuditOutcome outcome) {
        if (outcome == AuditOutcome.DENIED || outcome == AuditOutcome.ERROR) {
            return action.isSecurityRelated() ? CRITICAL : HIGH;
        }
        if (action == AuditAction.PHI_DISCLOSURE || action == AuditAction.DATA_PURGE) {
            return CRITICAL;
        }
        if (action.isPhiRelated()) {
            return MEDIUM;
        }
        if (action.isSecurityRelated()) {
            return HIGH;
        }
        return LOW;
    }
}
