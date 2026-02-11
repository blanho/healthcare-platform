package com.healthcare.notification.repository;

import com.healthcare.notification.domain.NotificationTemplate;
import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {

    Optional<NotificationTemplate> findByTemplateCode(String templateCode);

    @Query("SELECT t FROM NotificationTemplate t WHERE t.templateCode = :code AND t.active = true")
    Optional<NotificationTemplate> findActiveByTemplateCode(@Param("code") String code);

    List<NotificationTemplate> findByCategoryAndTypeAndActiveTrue(
        NotificationCategory category,
        NotificationType type
    );

    List<NotificationTemplate> findByActiveTrue();

    List<NotificationTemplate> findByCategoryAndActiveTrue(NotificationCategory category);

    boolean existsByTemplateCode(String templateCode);
}
