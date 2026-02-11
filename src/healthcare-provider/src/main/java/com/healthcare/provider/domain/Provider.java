package com.healthcare.provider.domain;

import com.healthcare.common.domain.AggregateRoot;
import com.healthcare.provider.domain.event.ProviderActivatedEvent;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "providers", indexes = {
    @Index(name = "idx_provider_email", columnList = "email"),
    @Index(name = "idx_provider_number", columnList = "provider_number"),
    @Index(name = "idx_provider_type", columnList = "provider_type"),
    @Index(name = "idx_provider_specialization", columnList = "specialization"),
    @Index(name = "idx_provider_name", columnList = "last_name, first_name")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Provider extends AggregateRoot {

    @NaturalId
    @Column(name = "provider_number", nullable = false, unique = true, length = 50)
    private String providerNumber;

    @Column(name = "first_name", nullable = false, length = 100)
    @Setter
    private String firstName;

    @Column(name = "middle_name", length = 100)
    @Setter
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    @Setter
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    @Setter
    private String email;

    @Column(name = "phone_number", length = 20)
    @Setter
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false, length = 50)
    @Setter
    private ProviderType providerType;

    @Column(name = "specialization", length = 100)
    @Setter
    private String specialization;

    @Embedded
    private MedicalLicense license;

    @Column(name = "npi_number", length = 10)
    @Setter
    private String npiNumber;

    @Column(name = "qualification", columnDefinition = "TEXT")
    @Setter
    private String qualification;

    @Column(name = "years_of_experience")
    @Setter
    private Integer yearsOfExperience;

    @Column(name = "consultation_fee", precision = 10, scale = 2)
    @Setter
    private BigDecimal consultationFee;

    @Column(name = "is_accepting_patients", nullable = false)
    private boolean acceptingPatients = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProviderStatus status;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProviderSchedule> schedules = new ArrayList<>();

    @Builder
    private Provider(
            String providerNumber,
            String firstName,
            String middleName,
            String lastName,
            String email,
            String phoneNumber,
            ProviderType providerType,
            String specialization,
            MedicalLicense license,
            String npiNumber,
            String qualification,
            Integer yearsOfExperience,
            BigDecimal consultationFee,
            boolean acceptingPatients,
            ProviderStatus status) {
        this.providerNumber = Objects.requireNonNull(providerNumber, "Provider number is required");
        this.firstName = Objects.requireNonNull(firstName, "First name is required");
        this.middleName = middleName;
        this.lastName = Objects.requireNonNull(lastName, "Last name is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.phoneNumber = phoneNumber;
        this.providerType = Objects.requireNonNull(providerType, "Provider type is required");
        this.specialization = specialization;
        this.license = Objects.requireNonNull(license, "License is required");
        this.npiNumber = npiNumber;
        this.qualification = qualification;
        this.yearsOfExperience = yearsOfExperience;
        this.consultationFee = consultationFee;
        this.acceptingPatients = acceptingPatients;
        this.status = status != null ? status : ProviderStatus.PENDING_VERIFICATION;
    }

    public String getFullName() {
        if (middleName != null && !middleName.isBlank()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + lastName;
    }

    public String getDisplayName() {
        String title = switch (providerType) {
            case DOCTOR, SPECIALIST, SURGEON -> "Dr.";
            case DENTIST -> "Dr.";
            default -> "";
        };
        return (title.isEmpty() ? "" : title + " ") + getFullName();
    }

    public void activate() {
        if (this.status == ProviderStatus.TERMINATED) {
            throw new IllegalStateException("Cannot activate a terminated provider");
        }
        if (!license.isValid()) {
            throw new IllegalStateException("Cannot activate provider with expired license");
        }
        this.status = ProviderStatus.ACTIVE;
        registerEvent(new ProviderActivatedEvent(this.getId(), this.providerNumber));
    }

    public void deactivate() {
        this.status = ProviderStatus.INACTIVE;
        this.acceptingPatients = false;
    }

    public void putOnLeave() {
        this.status = ProviderStatus.ON_LEAVE;
        this.acceptingPatients = false;
    }

    public void returnFromLeave() {
        if (this.status != ProviderStatus.ON_LEAVE) {
            throw new IllegalStateException("Provider is not on leave");
        }
        if (!license.isValid()) {
            throw new IllegalStateException("Cannot return from leave with expired license");
        }
        this.status = ProviderStatus.ACTIVE;
    }

    public void suspend() {
        this.status = ProviderStatus.SUSPENDED;
        this.acceptingPatients = false;
    }

    public void terminate() {
        this.status = ProviderStatus.TERMINATED;
        this.acceptingPatients = false;
        markAsDeleted();
    }

    public void startAcceptingPatients() {
        if (!status.canAcceptPatients()) {
            throw new IllegalStateException("Provider cannot accept patients in status: " + status);
        }
        this.acceptingPatients = true;
    }

    public void stopAcceptingPatients() {
        this.acceptingPatients = false;
    }

    public void updateLicense(MedicalLicense newLicense) {
        Objects.requireNonNull(newLicense, "License is required");
        this.license = newLicense;

        if (newLicense.isValid() && this.status == ProviderStatus.PENDING_VERIFICATION) {

        }
    }

    public boolean canBeScheduled() {
        return status.canBeScheduled() && license.isValid();
    }

    public boolean needsLicenseRenewal() {
        return license.expiresWithinDays(90);
    }

    public List<ProviderSchedule> getSchedules() {
        return Collections.unmodifiableList(schedules);
    }

    public List<ProviderSchedule> getActiveSchedules() {
        return schedules.stream()
            .filter(ProviderSchedule::isActive)
            .toList();
    }

    public Optional<ProviderSchedule> getScheduleForDay(DayOfWeek dayOfWeek) {
        return schedules.stream()
            .filter(s -> s.getDayOfWeek() == dayOfWeek && s.isActive())
            .findFirst();
    }

    public ProviderSchedule addSchedule(ProviderSchedule schedule) {
        Objects.requireNonNull(schedule, "Schedule is required");

        boolean dayExists = schedules.stream()
            .anyMatch(s -> s.getDayOfWeek() == schedule.getDayOfWeek());
        if (dayExists) {
            throw new IllegalStateException("Schedule already exists for " + schedule.getDayOfWeek());
        }

        schedule.setProvider(this);
        schedules.add(schedule);
        return schedule;
    }

    public void removeSchedule(ProviderSchedule schedule) {
        schedules.remove(schedule);
    }

    public void clearSchedules() {
        schedules.clear();
    }

    public boolean worksOnDay(DayOfWeek dayOfWeek) {
        return getScheduleForDay(dayOfWeek).isPresent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider provider = (Provider) o;
        return Objects.equals(providerNumber, provider.providerNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerNumber);
    }
}
