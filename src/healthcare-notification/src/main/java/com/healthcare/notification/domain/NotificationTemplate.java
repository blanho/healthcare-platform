package com.healthcare.notification.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_templates", indexes = {
    @Index(name = "idx_template_code", columnList = "template_code", unique = true),
    @Index(name = "idx_template_category_type", columnList = "category, type")
})
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "template_code", nullable = false, unique = true, length = 100)
    private String templateCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private NotificationCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "subject_template")
    private String subjectTemplate;

    @Column(name = "body_template", nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;

    @Column(name = "html_template", columnDefinition = "TEXT")
    private String htmlTemplate;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "locale", length = 10)
    private String locale;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected NotificationTemplate() {

    }

    public NotificationTemplate(
            String templateCode,
            String name,
            NotificationCategory category,
            NotificationType type,
            String subjectTemplate,
            String bodyTemplate) {
        this.templateCode = templateCode;
        this.name = name;
        this.category = category;
        this.type = type;
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
        this.active = true;
        this.locale = "en";
        this.createdAt = Instant.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void updateContent(String subjectTemplate, String bodyTemplate, String htmlTemplate) {
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
        this.htmlTemplate = htmlTemplate;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getTemplateCode() { return templateCode; }
    public String getName() { return name; }
    public NotificationCategory getCategory() { return category; }
    public NotificationType getType() { return type; }
    public String getSubjectTemplate() { return subjectTemplate; }
    public String getBodyTemplate() { return bodyTemplate; }
    public String getHtmlTemplate() { return htmlTemplate; }
    public boolean isActive() { return active; }
    public String getLocale() { return locale; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
