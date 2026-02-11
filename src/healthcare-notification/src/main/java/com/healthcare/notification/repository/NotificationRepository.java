package com.healthcare.notification.repository;

import com.healthcare.notification.domain.Notification;
import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.domain.NotificationStatus;
import com.healthcare.notification.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID>,
                                                JpaSpecificationExecutor<Notification> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(
        UUID userId,
        NotificationStatus status,
        Pageable pageable
    );

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId " +
           "AND n.status IN ('SENT', 'DELIVERED') ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId " +
           "AND n.status IN ('SENT', 'DELIVERED')")
    long countUnreadByUserId(@Param("userId") UUID userId);

    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' " +
           "OR (n.status = 'SCHEDULED' AND n.scheduledAt <= :now)")
    List<Notification> findReadyToSend(@Param("now") Instant now);

    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' " +
           "AND n.retryCount < :maxRetries")
    List<Notification> findRetryable(@Param("maxRetries") int maxRetries);

    Page<Notification> findByUserIdAndCategoryOrderByCreatedAtDesc(
        UUID userId,
        NotificationCategory category,
        Pageable pageable
    );

    Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(
        UUID userId,
        NotificationType type,
        Pageable pageable
    );

    Page<Notification> findByPatientIdOrderByCreatedAtDesc(UUID patientId, Pageable pageable);

    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', n.readAt = :now " +
           "WHERE n.userId = :userId AND n.status IN ('SENT', 'DELIVERED')")
    int markAllAsRead(@Param("userId") UUID userId, @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoff " +
           "AND n.status IN ('READ', 'CANCELLED')")
    int deleteOldNotifications(@Param("cutoff") Instant cutoff);

    @Query("SELECT n FROM Notification n WHERE n.status = 'SCHEDULED' " +
           "AND n.scheduledAt BETWEEN :start AND :end ORDER BY n.scheduledAt")
    List<Notification> findScheduledBetween(
        @Param("start") Instant start,
        @Param("end") Instant end
    );

    @Query("SELECT n.status, COUNT(n) FROM Notification n " +
           "WHERE n.createdAt >= :since GROUP BY n.status")
    List<Object[]> countByStatusSince(@Param("since") Instant since);

    List<Notification> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);
}
