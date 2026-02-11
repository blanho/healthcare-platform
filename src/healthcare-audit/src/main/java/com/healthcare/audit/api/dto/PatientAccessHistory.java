package com.healthcare.audit.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PatientAccessHistory(
    UUID patientId,
    Instant periodStart,
    Instant periodEnd,

    long totalAccessCount,
    long viewCount,
    long modificationCount,
    long exportCount,
    long printCount,

    long uniqueAccessorCount,
    List<String> accessorRoles,

    List<AuditEventSummary> accessEvents
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID patientId;
        private Instant periodStart;
        private Instant periodEnd;
        private long totalAccessCount;
        private long viewCount;
        private long modificationCount;
        private long exportCount;
        private long printCount;
        private long uniqueAccessorCount;
        private List<String> accessorRoles;
        private List<AuditEventSummary> accessEvents;

        public Builder patientId(UUID patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder periodStart(Instant periodStart) {
            this.periodStart = periodStart;
            return this;
        }

        public Builder periodEnd(Instant periodEnd) {
            this.periodEnd = periodEnd;
            return this;
        }

        public Builder totalAccessCount(long totalAccessCount) {
            this.totalAccessCount = totalAccessCount;
            return this;
        }

        public Builder viewCount(long viewCount) {
            this.viewCount = viewCount;
            return this;
        }

        public Builder modificationCount(long modificationCount) {
            this.modificationCount = modificationCount;
            return this;
        }

        public Builder exportCount(long exportCount) {
            this.exportCount = exportCount;
            return this;
        }

        public Builder printCount(long printCount) {
            this.printCount = printCount;
            return this;
        }

        public Builder uniqueAccessorCount(long uniqueAccessorCount) {
            this.uniqueAccessorCount = uniqueAccessorCount;
            return this;
        }

        public Builder accessorRoles(List<String> accessorRoles) {
            this.accessorRoles = accessorRoles;
            return this;
        }

        public Builder accessEvents(List<AuditEventSummary> accessEvents) {
            this.accessEvents = accessEvents;
            return this;
        }

        public PatientAccessHistory build() {
            return new PatientAccessHistory(
                patientId, periodStart, periodEnd,
                totalAccessCount, viewCount, modificationCount, exportCount, printCount,
                uniqueAccessorCount, accessorRoles, accessEvents
            );
        }
    }
}
