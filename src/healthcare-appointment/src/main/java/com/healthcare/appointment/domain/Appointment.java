package com.healthcare.appointment.domain;

import com.healthcare.appointment.domain.event.AppointmentCancelledEvent;
import com.healthcare.appointment.domain.event.AppointmentCheckedInEvent;
import com.healthcare.appointment.domain.event.AppointmentCompletedEvent;
import com.healthcare.appointment.domain.event.AppointmentConfirmedEvent;
import com.healthcare.appointment.domain.event.AppointmentNoShowEvent;
import com.healthcare.appointment.domain.event.AppointmentRescheduledEvent;
import com.healthcare.appointment.domain.event.AppointmentScheduledEvent;
import com.healthcare.common.domain.AggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "appointments", indexes = {
    @Index(name = "idx_appointment_patient", columnList = "patient_id"),
    @Index(name = "idx_appointment_provider", columnList = "provider_id"),
    @Index(name = "idx_appointment_date", columnList = "scheduled_date"),
    @Index(name = "idx_appointment_status", columnList = "status"),
    @Index(name = "idx_appointment_provider_date", columnList = "provider_id, scheduled_date")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Appointment extends AggregateRoot {

    @NaturalId
    @Column(name = "appointment_number", nullable = false, unique = true, length = 50)
    private String appointmentNumber;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Embedded
    private TimeSlot timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false, length = 50)
    @Setter
    private AppointmentType appointmentType;

    @Column(name = "reason_for_visit", columnDefinition = "TEXT")
    @Setter
    private String reasonForVisit;

    @Column(name = "notes", columnDefinition = "TEXT")
    @Setter
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancelled_by", length = 255)
    private String cancelledBy;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "checked_in_at")
    private Instant checkedInAt;

    @Column(name = "check_in_notes", columnDefinition = "TEXT")
    private String checkInNotes;

    @Column(name = "checked_out_at")
    private Instant checkedOutAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "completion_notes", columnDefinition = "TEXT")
    private String completionNotes;

    @Column(name = "cancelled_by_patient")
    private Boolean cancelledByPatient;

    public boolean isCancelledByPatient() {
        return Boolean.TRUE.equals(cancelledByPatient);
    }

    public static Appointment schedule(
            String appointmentNumber,
            UUID patientId,
            UUID providerId,
            TimeSlot timeSlot,
            AppointmentType appointmentType,
            String reasonForVisit,
            String notes) {
        return Appointment.builder()
                .appointmentNumber(appointmentNumber)
                .patientId(patientId)
                .providerId(providerId)
                .scheduledDate(timeSlot.getScheduledDate())
                .startTime(timeSlot.getStartTime())
                .durationMinutes(timeSlot.getDurationMinutes())
                .appointmentType(appointmentType)
                .reasonForVisit(reasonForVisit)
                .notes(notes)
                .build();
    }

    @Builder
    private Appointment(
            String appointmentNumber,
            UUID patientId,
            UUID providerId,
            LocalDate scheduledDate,
            LocalTime startTime,
            int durationMinutes,
            AppointmentType appointmentType,
            String reasonForVisit,
            String notes) {
        this.appointmentNumber = Objects.requireNonNull(appointmentNumber, "Appointment number is required");
        this.patientId = Objects.requireNonNull(patientId, "Patient ID is required");
        this.providerId = Objects.requireNonNull(providerId, "Provider ID is required");
        this.appointmentType = Objects.requireNonNull(appointmentType, "Appointment type is required");
        this.reasonForVisit = reasonForVisit;
        this.notes = notes;
        this.status = AppointmentStatus.SCHEDULED;

        int duration = durationMinutes > 0 ? durationMinutes : appointmentType.getDefaultDurationMinutes();
        this.timeSlot = TimeSlot.of(
            Objects.requireNonNull(scheduledDate, "Scheduled date is required"),
            Objects.requireNonNull(startTime, "Start time is required"),
            duration
        );

        registerEvent(new AppointmentScheduledEvent(this.getId(), this.appointmentNumber, patientId, providerId, timeSlot));
    }

    public void confirm() {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled appointments can be confirmed");
        }
        this.status = AppointmentStatus.CONFIRMED;
        registerEvent(new AppointmentConfirmedEvent(getId(), appointmentNumber));
    }

    public void checkIn(String notes) {
        if (!status.canCheckIn()) {
            throw new IllegalStateException("Cannot check in with status: " + status);
        }
        this.status = AppointmentStatus.CHECKED_IN;
        this.checkedInAt = Instant.now();
        this.checkInNotes = notes;
        registerEvent(new AppointmentCheckedInEvent(getId(), appointmentNumber, patientId));
    }

    public void start() {
        if (status != AppointmentStatus.CHECKED_IN) {
            throw new IllegalStateException("Patient must check in before starting appointment");
        }
        this.status = AppointmentStatus.IN_PROGRESS;
    }

    public void complete(String notes) {
        if (status != AppointmentStatus.IN_PROGRESS && status != AppointmentStatus.CHECKED_IN) {
            throw new IllegalStateException("Cannot complete appointment with status: " + status);
        }
        this.status = AppointmentStatus.COMPLETED;
        this.completedAt = Instant.now();
        this.checkedOutAt = Instant.now();
        this.completionNotes = notes;
        registerEvent(new AppointmentCompletedEvent(getId(), appointmentNumber, patientId, providerId));
    }

    public void cancel(String reason, boolean cancelledByPatient) {
        if (!status.canCancel()) {
            throw new IllegalStateException("Cannot cancel appointment with status: " + status);
        }
        this.status = AppointmentStatus.CANCELLED;
        this.cancelledAt = Instant.now();
        this.cancellationReason = reason;
        this.cancelledByPatient = cancelledByPatient;
        registerEvent(new AppointmentCancelledEvent(getId(), appointmentNumber, patientId, providerId, reason));
    }

    public void markNoShow() {
        if (!status.isActive()) {
            throw new IllegalStateException("Cannot mark no-show for non-active appointment");
        }
        this.status = AppointmentStatus.NO_SHOW;
        registerEvent(new AppointmentNoShowEvent(getId(), appointmentNumber, patientId));
    }

    public void reschedule(TimeSlot newTimeSlot) {
        if (!status.canReschedule()) {
            throw new IllegalStateException("Cannot reschedule appointment with status: " + status);
        }

        TimeSlot oldSlot = this.timeSlot;
        this.timeSlot = newTimeSlot;
        this.status = AppointmentStatus.RESCHEDULED;

        registerEvent(new AppointmentRescheduledEvent(getId(), appointmentNumber, oldSlot, this.timeSlot));
    }

    public void reschedule(LocalDate newDate, LocalTime newStartTime, int durationMinutes) {
        int duration = durationMinutes > 0 ? durationMinutes : timeSlot.getDurationMinutes();
        reschedule(TimeSlot.of(newDate, newStartTime, duration));
    }

    public boolean overlaps(TimeSlot other) {
        return this.timeSlot.overlaps(other);
    }

    public boolean isToday() {
        return timeSlot.isToday();
    }

    public boolean isPast() {
        return timeSlot.isPast();
    }

    public LocalDate getScheduledDate() {
        return timeSlot.getDate();
    }

    public LocalTime getStartTime() {
        return timeSlot.getStartTime();
    }

    public LocalTime getEndTime() {
        return timeSlot.getEndTime();
    }

    public int getDurationMinutes() {
        return timeSlot.getDurationMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(appointmentNumber, that.appointmentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentNumber);
    }
}
