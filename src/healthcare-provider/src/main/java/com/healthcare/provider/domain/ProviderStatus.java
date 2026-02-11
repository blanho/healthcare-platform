package com.healthcare.provider.domain;

public enum ProviderStatus {

    ACTIVE("Active"),

    INACTIVE("Inactive"),

    ON_LEAVE("On Leave"),

    PENDING_VERIFICATION("Pending Verification"),

    SUSPENDED("Suspended"),

    TERMINATED("Terminated");

    private final String displayName;

    ProviderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canAcceptPatients() {
        return this == ACTIVE;
    }

    public boolean canBeScheduled() {
        return this == ACTIVE;
    }
}
