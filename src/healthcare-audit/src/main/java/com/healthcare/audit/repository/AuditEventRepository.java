package com.healthcare.audit.repository;

import com.healthcare.audit.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {

    Page<AuditEvent> findByUserIdOrderByEventTimestampDesc(UUID userId, Pageable pageable);

    @Query("SELECT a FROM AuditEvent a WHERE a.userId = :userId " +
           "AND a.eventTimestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY a.eventTimestamp DESC")
    Page<AuditEvent> findByUserIdAndTimeRange(
        @Param("userId") UUID userId,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        Pageable pageable
    );

    @Query("SELECT a FROM AuditEvent a WHERE a.userId = :userId " +
           "AND a.resourceCategory IN :phiCategories " +
           "ORDER BY a.eventTimestamp DESC")
    Page<AuditEvent> findUserPhiAccess(
        @Param("userId") UUID userId,
        @Param("phiCategories") List<ResourceCategory> phiCategories,
        Pageable pageable
    );

    Page<AuditEvent> findByResourceCategoryAndResourceIdOrderByEventTimestampDesc(
        ResourceCategory category, UUID resourceId, Pageable pageable
    );

    Page<AuditEvent> findByPatientIdOrderByEventTimestampDesc(UUID patientId, Pageable pageable);

    @Query("SELECT a FROM AuditEvent a WHERE a.patientId = :patientId " +
           "AND a.eventTimestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY a.eventTimestamp DESC")
    Page<AuditEvent> findByPatientIdAndTimeRange(
        @Param("patientId") UUID patientId,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        Pageable pageable
    );

    Page<AuditEvent> findByActionOrderByEventTimestampDesc(AuditAction action, Pageable pageable);

    @Query("SELECT a FROM AuditEvent a WHERE a.outcome IN :outcomes " +
           "AND a.eventTimestamp >= :since " +
           "ORDER BY a.eventTimestamp DESC")
    Page<AuditEvent> findFailedActions(
        @Param("outcomes") List<AuditOutcome> outcomes,
        @Param("since") Instant since,
        Pageable pageable
    );

    @Query("SELECT a FROM AuditEvent a WHERE a.action IN :securityActions " +
           "AND a.eventTimestamp >= :since " +
           "ORDER BY a.eventTimestamp DESC")
    Page<AuditEvent> findSecurityEvents(
        @Param("securityActions") List<AuditAction> securityActions,
        @Param("since") Instant since,
        Pageable pageable
    );

    @Query("SELECT a FROM AuditEvent a WHERE a.eventTimestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY a.eventTimestamp DESC")
    Page<AuditEvent> findByTimeRange(
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        Pageable pageable
    );

    @Query("SELECT a FROM AuditEvent a WHERE a.eventTimestamp >= :since " +
           "ORDER BY a.eventTimestamp DESC")
    Page<AuditEvent> findRecentEvents(@Param("since") Instant since, Pageable pageable);

    List<AuditEvent> findByCorrelationIdOrderByEventTimestampAsc(String correlationId);

    List<AuditEvent> findBySessionIdOrderByEventTimestampAsc(String sessionId);

    @Query("SELECT COUNT(a) FROM AuditEvent a WHERE a.userId = :userId " +
           "AND a.eventTimestamp >= :since")
    long countUserEventsSince(@Param("userId") UUID userId, @Param("since") Instant since);

    @Query("SELECT COUNT(a) FROM AuditEvent a WHERE a.userId = :userId " +
           "AND a.resourceCategory IN :phiCategories " +
           "AND a.eventTimestamp >= :since")
    long countUserPhiAccessSince(
        @Param("userId") UUID userId,
        @Param("phiCategories") List<ResourceCategory> phiCategories,
        @Param("since") Instant since
    );

    @Query("SELECT COUNT(a) FROM AuditEvent a WHERE a.userId = :userId " +
           "AND a.action = :action " +
           "AND a.eventTimestamp >= :since")
    long countUserActionSince(
        @Param("userId") UUID userId,
        @Param("action") AuditAction action,
        @Param("since") Instant since
    );

    @Query("SELECT a.userId, COUNT(a) as eventCount FROM AuditEvent a " +
           "WHERE a.eventTimestamp >= :since " +
           "GROUP BY a.userId " +
           "HAVING COUNT(a) > :threshold " +
           "ORDER BY eventCount DESC")
    List<Object[]> findUsersWithHighActivity(
        @Param("since") Instant since,
        @Param("threshold") long threshold
    );

    @Query(value = "SELECT * FROM audit_events a " +
           "WHERE a.resource_category IN :phiCategories " +
           "AND a.event_timestamp >= :since " +
           "AND (EXTRACT(HOUR FROM a.event_timestamp) < :startHour " +
           "     OR EXTRACT(HOUR FROM a.event_timestamp) >= :endHour) " +
           "ORDER BY a.event_timestamp DESC",
           nativeQuery = true)
    Page<AuditEvent> findAfterHoursPhiAccess(
        @Param("phiCategories") List<String> phiCategories,
        @Param("since") Instant since,
        @Param("startHour") int startHour,
        @Param("endHour") int endHour,
        Pageable pageable
    );

    @Query("SELECT a.resourceCategory, COUNT(a) FROM AuditEvent a " +
           "WHERE a.eventTimestamp BETWEEN :startTime AND :endTime " +
           "GROUP BY a.resourceCategory")
    List<Object[]> countByResourceCategory(
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime
    );

    @Query("SELECT a.action, COUNT(a) FROM AuditEvent a " +
           "WHERE a.eventTimestamp BETWEEN :startTime AND :endTime " +
           "GROUP BY a.action")
    List<Object[]> countByAction(
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime
    );

    @Query("SELECT a.outcome, COUNT(a) FROM AuditEvent a " +
           "WHERE a.eventTimestamp BETWEEN :startTime AND :endTime " +
           "GROUP BY a.outcome")
    List<Object[]> countByOutcome(
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime
    );

    @Query("SELECT a.severity, COUNT(a) FROM AuditEvent a " +
           "WHERE a.eventTimestamp BETWEEN :startTime AND :endTime " +
           "GROUP BY a.severity")
    List<Object[]> countBySeverity(
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime
    );

    @Query("SELECT COUNT(a) FROM AuditEvent a " +
           "WHERE a.resourceCategory IN :phiCategories " +
           "AND a.eventTimestamp BETWEEN :startTime AND :endTime")
    long countPhiAccessInRange(
        @Param("phiCategories") List<ResourceCategory> phiCategories,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime
    );

    @Query("SELECT DISTINCT a.userId FROM AuditEvent a " +
           "WHERE a.resourceCategory IN :phiCategories " +
           "AND a.eventTimestamp BETWEEN :startTime AND :endTime")
    List<UUID> findUsersWhoAccessedPhi(
        @Param("phiCategories") List<ResourceCategory> phiCategories,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime
    );

    @Query("SELECT a FROM AuditEvent a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:patientId IS NULL OR a.patientId = :patientId) AND " +
           "(:resourceCategory IS NULL OR a.resourceCategory = :resourceCategory) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:outcome IS NULL OR a.outcome = :outcome) AND " +
           "(:severity IS NULL OR a.severity = :severity) AND " +
           "a.eventTimestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY a.eventTimestamp DESC")
    Page<AuditEvent> searchAuditEvents(
        @Param("userId") UUID userId,
        @Param("patientId") UUID patientId,
        @Param("resourceCategory") ResourceCategory resourceCategory,
        @Param("action") AuditAction action,
        @Param("outcome") AuditOutcome outcome,
        @Param("severity") AuditSeverity severity,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        Pageable pageable
    );
}
