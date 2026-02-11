package com.healthcare.medicalrecord.domain;

public enum RecordStatus {

    DRAFT,

    FINALIZED,

    AMENDED,

    VOIDED;

    public boolean canEdit() {
        return this == DRAFT;
    }

    public boolean canFinalize() {
        return this == DRAFT;
    }

    public boolean canAmend() {
        return this == FINALIZED || this == AMENDED;
    }

    public boolean canVoid() {
        return this != VOIDED;
    }

    public boolean isTerminal() {
        return this == VOIDED;
    }
}
