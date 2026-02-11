package com.healthcare.audit.domain;

public enum AuditOutcome {
    SUCCESS("SUCCESS", "Action completed successfully"),
    FAILURE("FAILURE", "Action failed"),
    PARTIAL("PARTIAL", "Action partially completed"),
    DENIED("DENIED", "Action denied due to authorization"),
    ERROR("ERROR", "Action failed due to error");

    private final String code;
    private final String description;

    AuditOutcome(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public boolean isSuccessful() {
        return this == SUCCESS || this == PARTIAL;
    }
}
