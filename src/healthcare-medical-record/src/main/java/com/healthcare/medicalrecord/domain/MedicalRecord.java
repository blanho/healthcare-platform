package com.healthcare.medicalrecord.domain;

import com.healthcare.common.domain.AggregateRoot;
import com.healthcare.medicalrecord.domain.event.CriticalVitalsDetectedEvent;
import com.healthcare.medicalrecord.domain.event.MedicalRecordAmendedEvent;
import com.healthcare.medicalrecord.domain.event.MedicalRecordCreatedEvent;
import com.healthcare.medicalrecord.domain.event.MedicalRecordFinalizedEvent;
import com.healthcare.medicalrecord.domain.event.MedicalRecordVoidedEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "medical_records", indexes = {
    @Index(name = "idx_medical_record_number", columnList = "record_number"),
    @Index(name = "idx_medical_record_patient", columnList = "patient_id"),
    @Index(name = "idx_medical_record_provider", columnList = "provider_id"),
    @Index(name = "idx_medical_record_appointment", columnList = "appointment_id"),
    @Index(name = "idx_medical_record_date", columnList = "record_date"),
    @Index(name = "idx_medical_record_type", columnList = "record_type")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicalRecord extends AggregateRoot {

    @NaturalId
    @Column(name = "record_number", nullable = false, unique = true, length = 50)
    private String recordNumber;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false, length = 50)
    private RecordType recordType;

    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;

    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    @Setter
    private String chiefComplaint;

    @Column(name = "notes", columnDefinition = "TEXT")
    @Setter
    private String notes;

    @Embedded
    @Setter
    private VitalSigns vitalSigns;

    @Embedded
    @Setter
    private SoapNote soapNote;

    @ElementCollection
    @CollectionTable(
        name = "medical_record_diagnoses",
        joinColumns = @JoinColumn(name = "medical_record_id")
    )
    private List<Diagnosis> diagnoses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status;

    @Column(name = "finalized_at")
    private Instant finalizedAt;

    @Column(name = "finalized_by", length = 255)
    private String finalizedBy;

    @Column(name = "attachments_count")
    private int attachmentsCount;

    @Builder
    private MedicalRecord(
            String recordNumber,
            UUID patientId,
            UUID providerId,
            UUID appointmentId,
            RecordType recordType,
            LocalDateTime recordDate,
            String chiefComplaint,
            String notes) {
        this.recordNumber = Objects.requireNonNull(recordNumber, "Record number is required");
        this.patientId = Objects.requireNonNull(patientId, "Patient ID is required");
        this.providerId = Objects.requireNonNull(providerId, "Provider ID is required");
        this.appointmentId = appointmentId;
        this.recordType = Objects.requireNonNull(recordType, "Record type is required");
        this.recordDate = Objects.requireNonNull(recordDate, "Record date is required");
        this.chiefComplaint = chiefComplaint;
        this.notes = notes;
        this.status = RecordStatus.DRAFT;
        this.attachmentsCount = 0;

        registerEvent(new MedicalRecordCreatedEvent(
            this.getId(), this.recordNumber, patientId, providerId, recordType
        ));
    }

    public static MedicalRecord create(
            String recordNumber,
            UUID patientId,
            UUID providerId,
            UUID appointmentId,
            RecordType recordType,
            LocalDateTime recordDate,
            String chiefComplaint) {
        return MedicalRecord.builder()
                .recordNumber(recordNumber)
                .patientId(patientId)
                .providerId(providerId)
                .appointmentId(appointmentId)
                .recordType(recordType)
                .recordDate(recordDate)
                .chiefComplaint(chiefComplaint)
                .build();
    }

    public void addDiagnosis(Diagnosis diagnosis) {
        Objects.requireNonNull(diagnosis, "Diagnosis is required");
        validateEditable();
        this.diagnoses.add(diagnosis);
    }

    public void removeDiagnosis(Diagnosis diagnosis) {
        validateEditable();
        this.diagnoses.remove(diagnosis);
    }

    public void clearDiagnoses() {
        validateEditable();
        this.diagnoses.clear();
    }

    public List<Diagnosis> getDiagnoses() {
        return Collections.unmodifiableList(diagnoses);
    }

    public void recordVitals(VitalSigns vitals) {
        validateEditable();
        this.vitalSigns = vitals;

        if (vitals != null && vitals.hasCriticalValue()) {
            registerEvent(new CriticalVitalsDetectedEvent(
                this.getId(), this.recordNumber, patientId, vitals
            ));
        }
    }

    public void updateSoapNote(SoapNote soapNote) {
        validateEditable();
        this.soapNote = soapNote;
    }

    public void finalize(String userId) {
        if (!status.canFinalize()) {
            throw new IllegalStateException("Cannot finalize record in status: " + status);
        }

        this.status = RecordStatus.FINALIZED;
        this.finalizedAt = Instant.now();
        this.finalizedBy = userId;

        registerEvent(new MedicalRecordFinalizedEvent(
            this.getId(), this.recordNumber, patientId, userId
        ));
    }

    public MedicalRecordAmendment amend(String reason, String amendedBy) {
        if (!status.canAmend()) {
            throw new IllegalStateException("Cannot amend record in status: " + status);
        }

        this.status = RecordStatus.AMENDED;

        MedicalRecordAmendment amendment = new MedicalRecordAmendment(
            this.getId(), reason, amendedBy
        );

        registerEvent(new MedicalRecordAmendedEvent(
            this.getId(), this.recordNumber, reason, amendedBy
        ));

        return amendment;
    }

    public void voidRecord(String reason, String voidedBy) {
        if (!status.canVoid()) {
            throw new IllegalStateException("Cannot void record in status: " + status);
        }

        this.status = RecordStatus.VOIDED;
        this.notes = (this.notes != null ? this.notes + "\n\n" : "")
                + "VOIDED: " + reason + " by " + voidedBy + " at " + Instant.now();

        registerEvent(new MedicalRecordVoidedEvent(
            this.getId(), this.recordNumber, reason, voidedBy
        ));
    }

    public void incrementAttachmentCount() {
        this.attachmentsCount++;
    }

    public void decrementAttachmentCount() {
        if (this.attachmentsCount > 0) {
            this.attachmentsCount--;
        }
    }

    public Diagnosis getPrimaryDiagnosis() {
        return diagnoses.stream()
                .filter(Diagnosis::isPrimary)
                .findFirst()
                .orElse(diagnoses.isEmpty() ? null : diagnoses.get(0));
    }

    public boolean hasCriticalVitals() {
        return vitalSigns != null && vitalSigns.hasCriticalValue();
    }

    public boolean isEditable() {
        return status.canEdit();
    }

    private void validateEditable() {
        if (!status.canEdit()) {
            throw new IllegalStateException("Cannot edit record in status: " + status);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalRecord that = (MedicalRecord) o;
        return Objects.equals(recordNumber, that.recordNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordNumber);
    }
}
