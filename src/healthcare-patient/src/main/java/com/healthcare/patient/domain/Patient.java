package com.healthcare.patient.domain;

import com.healthcare.common.domain.AggregateRoot;
import com.healthcare.common.crypto.EncryptedStringConverter;
import com.healthcare.patient.domain.event.PatientActivatedEvent;
import com.healthcare.patient.domain.event.PatientDeactivatedEvent;
import com.healthcare.patient.domain.event.PatientTransferredEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Entity
@Table(name = "patients", indexes = {
    @Index(name = "idx_patient_email", columnList = "email"),
    @Index(name = "idx_patient_phone", columnList = "phone_number"),
    @Index(name = "idx_patient_mrn", columnList = "medical_record_number"),
    @Index(name = "idx_patient_status", columnList = "status"),
    @Index(name = "idx_patient_name", columnList = "last_name, first_name")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Patient extends AggregateRoot {

    @Column(name = "first_name", nullable = false, length = 100)
    @Setter
    private String firstName;

    @Column(name = "middle_name", length = 100)
    @Setter
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    @Setter
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    @Setter
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    @Setter
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", length = 10)
    @Setter
    private BloodType bloodType;

    @Column(name = "email", nullable = false, length = 255)
    @Setter
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    @Setter
    private String phoneNumber;

    @Column(name = "secondary_phone", length = 20)
    @Setter
    private String secondaryPhone;

    @NaturalId
    @Column(name = "medical_record_number", nullable = false, unique = true, length = 50)
    private String medicalRecordNumber;

    @Column(name = "ssn", length = 255)
    @Setter
    @Convert(converter = EncryptedStringConverter.class)
    private String socialSecurityNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PatientStatus status;

    @Embedded
    @Setter
    private Address address;

    @Embedded
    @Setter
    private Insurance insurance;

    @Embedded
    @Setter
    private EmergencyContact emergencyContact;

    @Builder
    private Patient(
            String firstName,
            String middleName,
            String lastName,
            LocalDate dateOfBirth,
            Gender gender,
            BloodType bloodType,
            String email,
            String phoneNumber,
            String secondaryPhone,
            String medicalRecordNumber,
            String socialSecurityNumber,
            PatientStatus status,
            Address address,
            Insurance insurance,
            EmergencyContact emergencyContact) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodType = bloodType;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.secondaryPhone = secondaryPhone;
        this.medicalRecordNumber = medicalRecordNumber;
        this.socialSecurityNumber = socialSecurityNumber;
        this.status = status != null ? status : PatientStatus.PENDING;
        this.address = address;
        this.insurance = insurance;
        this.emergencyContact = emergencyContact;
    }

    public String getFullName() {
        if (middleName != null && !middleName.isBlank()) {
            return String.format("%s %s %s", firstName, middleName, lastName);
        }
        return String.format("%s %s", firstName, lastName);
    }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public boolean isMinor() {
        return getAge() < 18;
    }

    public boolean hasActiveInsurance() {
        return insurance != null && insurance.isActive();
    }

    public void activate() {
        if (this.status == PatientStatus.DECEASED) {
            throw new IllegalStateException("Cannot activate deceased patient");
        }
        this.status = PatientStatus.ACTIVE;
        registerEvent(new PatientActivatedEvent(this.getId(), this.medicalRecordNumber));
    }

    public void deactivate() {
        this.status = PatientStatus.INACTIVE;
        registerEvent(new PatientDeactivatedEvent(this.getId(), this.medicalRecordNumber));
    }

    public void transfer(String newFacility) {
        this.status = PatientStatus.TRANSFERRED;
        registerEvent(new PatientTransferredEvent(this.getId(), this.medicalRecordNumber, newFacility));
    }

    public boolean canScheduleAppointments() {
        return this.status == PatientStatus.ACTIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(medicalRecordNumber, patient.medicalRecordNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medicalRecordNumber);
    }
}
