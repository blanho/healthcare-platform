package com.healthcare.appointment.repository;

import com.healthcare.appointment.domain.Appointment;
import com.healthcare.appointment.domain.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID>, JpaSpecificationExecutor<Appointment> {

    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    boolean existsByAppointmentNumber(String appointmentNumber);

    Page<Appointment> findByDeletedFalse(Pageable pageable);

    Page<Appointment> findByPatientIdAndDeletedFalse(UUID patientId, Pageable pageable);

    Page<Appointment> findByProviderIdAndDeletedFalse(UUID providerId, Pageable pageable);

    Page<Appointment> findByStatusAndDeletedFalse(AppointmentStatus status, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.providerId = :providerId " +
           "AND a.timeSlot.date = :date AND a.deleted = false " +
           "ORDER BY a.timeSlot.startTime")
    List<Appointment> findByProviderIdAndDate(
        @Param("providerId") UUID providerId,
        @Param("date") LocalDate date
    );

    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId " +
           "AND a.timeSlot.date = :date AND a.deleted = false " +
           "ORDER BY a.timeSlot.startTime")
    List<Appointment> findByPatientIdAndDate(
        @Param("patientId") UUID patientId,
        @Param("date") LocalDate date
    );

    @Query("SELECT a FROM Appointment a WHERE a.timeSlot.date >= :startDate " +
           "AND a.timeSlot.date <= :endDate AND a.deleted = false " +
           "ORDER BY a.timeSlot.date, a.timeSlot.startTime")
    Page<Appointment> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );

    @Query("SELECT a FROM Appointment a WHERE a.providerId = :providerId " +
           "AND a.timeSlot.date = CURRENT_DATE AND a.deleted = false " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED', 'CHECKED_IN', 'IN_PROGRESS') " +
           "ORDER BY a.timeSlot.startTime")
    List<Appointment> findTodaysAppointmentsForProvider(@Param("providerId") UUID providerId);

    @Query("SELECT a FROM Appointment a WHERE a.providerId = :providerId " +
           "AND a.timeSlot.date = :date AND a.deleted = false " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED', 'CHECKED_IN', 'IN_PROGRESS') " +
           "ORDER BY a.timeSlot.startTime")
    List<Appointment> findTodaysAppointments(@Param("providerId") UUID providerId, @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId AND a.deleted = false " +
           "ORDER BY a.timeSlot.date DESC, a.timeSlot.startTime DESC")
    Page<Appointment> findByPatientIdOrderByDateDesc(@Param("patientId") UUID patientId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.providerId = :providerId AND a.deleted = false " +
           "ORDER BY a.timeSlot.date DESC, a.timeSlot.startTime DESC")
    Page<Appointment> findByProviderIdOrderByDateDesc(@Param("providerId") UUID providerId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId " +
           "AND a.timeSlot.date >= CURRENT_DATE AND a.deleted = false " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
           "ORDER BY a.timeSlot.date, a.timeSlot.startTime")
    Page<Appointment> findUpcomingAppointmentsForPatient(
        @Param("patientId") UUID patientId,
        Pageable pageable
    );

    @Query("SELECT a FROM Appointment a WHERE a.providerId = :providerId " +
           "AND a.timeSlot.date = :date " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED', 'CHECKED_IN', 'IN_PROGRESS') " +
           "AND a.deleted = false " +
           "AND ((a.timeSlot.startTime < :endTime AND a.timeSlot.endTime > :startTime))")
    List<Appointment> findOverlappingAppointments(
        @Param("providerId") UUID providerId,
        @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.providerId = :providerId " +
           "AND a.timeSlot.date = :date AND a.status = :status AND a.deleted = false")
    long countByProviderAndDateAndStatus(
        @Param("providerId") UUID providerId,
        @Param("date") LocalDate date,
        @Param("status") AppointmentStatus status
    );

    long countByPatientIdAndDeletedFalse(UUID patientId);

    long countByProviderIdAndDeletedFalse(UUID providerId);

    @Query("SELECT a FROM Appointment a WHERE a.timeSlot.date = :tomorrow " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED') AND a.deleted = false")
    List<Appointment> findAppointmentsNeedingReminders(@Param("tomorrow") LocalDate tomorrow);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.timeSlot.date = :date AND a.deleted = false")
    long countByTimeSlotScheduledDate(@Param("date") LocalDate date);

    long countByStatus(AppointmentStatus status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status " +
           "AND a.timeSlot.date >= :startDate AND a.timeSlot.date <= :endDate AND a.deleted = false")
    long countByStatusAndTimeSlotScheduledDateBetween(
        @Param("status") AppointmentStatus status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.timeSlot.date >= :startDate " +
           "AND a.timeSlot.date <= :endDate AND a.deleted = false")
    long countByTimeSlotScheduledDateBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT a FROM Appointment a WHERE a.timeSlot.date >= :date " +
           "AND a.status IN :statuses AND a.deleted = false " +
           "ORDER BY a.timeSlot.date, a.timeSlot.startTime")
    List<Appointment> findByTimeSlotScheduledDateGreaterThanEqualAndStatusInOrderByTimeSlotScheduledDateAscTimeSlotStartTimeAsc(
        @Param("date") LocalDate date,
        @Param("statuses") List<AppointmentStatus> statuses,
        Pageable pageable
    );
}
