package com.healthcare.audit.api.dto;

import java.time.Instant;
import java.util.Map;

public record ComplianceReportResponse(

    String reportType,
    Instant startTime,
    Instant endTime,
    Instant generatedAt,

    long totalEvents,
    long phiAccessEvents,
    long securityEvents,
    long failedEvents,
    long criticalEvents,

    long uniquePhiAccessors,
    long afterHoursAccess,

    long uniqueUsers,
    Map<String, Long> topPhiAccessors,

    Map<String, Long> eventsByCategory,
    Map<String, Long> eventsByAction,
    Map<String, Long> eventsByOutcome,
    Map<String, Long> eventsBySeverity,

    long anomalousActivities,
    Map<String, Long> anomalyDetails
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String reportType;
        private Instant startTime;
        private Instant endTime;
        private Instant generatedAt = Instant.now();
        private long totalEvents;
        private long phiAccessEvents;
        private long securityEvents;
        private long failedEvents;
        private long criticalEvents;
        private long uniquePhiAccessors;
        private long afterHoursAccess;
        private long uniqueUsers;
        private Map<String, Long> topPhiAccessors;
        private Map<String, Long> eventsByCategory;
        private Map<String, Long> eventsByAction;
        private Map<String, Long> eventsByOutcome;
        private Map<String, Long> eventsBySeverity;
        private long anomalousActivities;
        private Map<String, Long> anomalyDetails;

        public Builder reportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder generatedAt(Instant generatedAt) {
            this.generatedAt = generatedAt;
            return this;
        }

        public Builder totalEvents(long totalEvents) {
            this.totalEvents = totalEvents;
            return this;
        }

        public Builder phiAccessEvents(long phiAccessEvents) {
            this.phiAccessEvents = phiAccessEvents;
            return this;
        }

        public Builder securityEvents(long securityEvents) {
            this.securityEvents = securityEvents;
            return this;
        }

        public Builder failedEvents(long failedEvents) {
            this.failedEvents = failedEvents;
            return this;
        }

        public Builder criticalEvents(long criticalEvents) {
            this.criticalEvents = criticalEvents;
            return this;
        }

        public Builder uniquePhiAccessors(long uniquePhiAccessors) {
            this.uniquePhiAccessors = uniquePhiAccessors;
            return this;
        }

        public Builder afterHoursAccess(long afterHoursAccess) {
            this.afterHoursAccess = afterHoursAccess;
            return this;
        }

        public Builder uniqueUsers(long uniqueUsers) {
            this.uniqueUsers = uniqueUsers;
            return this;
        }

        public Builder topPhiAccessors(Map<String, Long> topPhiAccessors) {
            this.topPhiAccessors = topPhiAccessors;
            return this;
        }

        public Builder eventsByCategory(Map<String, Long> eventsByCategory) {
            this.eventsByCategory = eventsByCategory;
            return this;
        }

        public Builder eventsByAction(Map<String, Long> eventsByAction) {
            this.eventsByAction = eventsByAction;
            return this;
        }

        public Builder eventsByOutcome(Map<String, Long> eventsByOutcome) {
            this.eventsByOutcome = eventsByOutcome;
            return this;
        }

        public Builder eventsBySeverity(Map<String, Long> eventsBySeverity) {
            this.eventsBySeverity = eventsBySeverity;
            return this;
        }

        public Builder anomalousActivities(long anomalousActivities) {
            this.anomalousActivities = anomalousActivities;
            return this;
        }

        public Builder anomalyDetails(Map<String, Long> anomalyDetails) {
            this.anomalyDetails = anomalyDetails;
            return this;
        }

        public ComplianceReportResponse build() {
            return new ComplianceReportResponse(
                reportType, startTime, endTime, generatedAt,
                totalEvents, phiAccessEvents, securityEvents, failedEvents, criticalEvents,
                uniquePhiAccessors, afterHoursAccess,
                uniqueUsers, topPhiAccessors,
                eventsByCategory, eventsByAction, eventsByOutcome, eventsBySeverity,
                anomalousActivities, anomalyDetails
            );
        }
    }
}
