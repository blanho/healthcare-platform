package com.healthcare.audit.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserActivitySummary(
    UUID userId,
    String username,
    String userRole,
    Instant periodStart,
    Instant periodEnd,

    long totalEvents,
    long phiAccessCount,
    long modificationCount,
    long exportCount,
    long loginCount,
    long failedLoginCount,

    long uniquePatientsAccessed,
    long uniqueRecordsAccessed,

    long afterHoursAccessCount,
    long deniedAccessCount,
    boolean hasAnomalousActivity,

    List<AuditEventSummary> recentEvents
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID userId;
        private String username;
        private String userRole;
        private Instant periodStart;
        private Instant periodEnd;
        private long totalEvents;
        private long phiAccessCount;
        private long modificationCount;
        private long exportCount;
        private long loginCount;
        private long failedLoginCount;
        private long uniquePatientsAccessed;
        private long uniqueRecordsAccessed;
        private long afterHoursAccessCount;
        private long deniedAccessCount;
        private boolean hasAnomalousActivity;
        private List<AuditEventSummary> recentEvents;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder userRole(String userRole) {
            this.userRole = userRole;
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

        public Builder totalEvents(long totalEvents) {
            this.totalEvents = totalEvents;
            return this;
        }

        public Builder phiAccessCount(long phiAccessCount) {
            this.phiAccessCount = phiAccessCount;
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

        public Builder loginCount(long loginCount) {
            this.loginCount = loginCount;
            return this;
        }

        public Builder failedLoginCount(long failedLoginCount) {
            this.failedLoginCount = failedLoginCount;
            return this;
        }

        public Builder uniquePatientsAccessed(long uniquePatientsAccessed) {
            this.uniquePatientsAccessed = uniquePatientsAccessed;
            return this;
        }

        public Builder uniqueRecordsAccessed(long uniqueRecordsAccessed) {
            this.uniqueRecordsAccessed = uniqueRecordsAccessed;
            return this;
        }

        public Builder afterHoursAccessCount(long afterHoursAccessCount) {
            this.afterHoursAccessCount = afterHoursAccessCount;
            return this;
        }

        public Builder deniedAccessCount(long deniedAccessCount) {
            this.deniedAccessCount = deniedAccessCount;
            return this;
        }

        public Builder hasAnomalousActivity(boolean hasAnomalousActivity) {
            this.hasAnomalousActivity = hasAnomalousActivity;
            return this;
        }

        public Builder recentEvents(List<AuditEventSummary> recentEvents) {
            this.recentEvents = recentEvents;
            return this;
        }

        public UserActivitySummary build() {
            return new UserActivitySummary(
                userId, username, userRole, periodStart, periodEnd,
                totalEvents, phiAccessCount, modificationCount, exportCount,
                loginCount, failedLoginCount,
                uniquePatientsAccessed, uniqueRecordsAccessed,
                afterHoursAccessCount, deniedAccessCount, hasAnomalousActivity,
                recentEvents
            );
        }
    }
}
