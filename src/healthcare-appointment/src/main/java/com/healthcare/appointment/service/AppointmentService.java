package com.healthcare.appointment.service;

import com.healthcare.appointment.api.dto.*;
import com.healthcare.appointment.domain.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {

    AppointmentResponse schedule(ScheduleAppointmentRequest request);

    AppointmentResponse getById(UUID appointmentId);

    AppointmentResponse getByAppointmentNumber(String appointmentNumber);

    Page<AppointmentSummaryResponse> search(AppointmentSearchCriteria criteria, Pageable pageable);

    Page<AppointmentSummaryResponse> getByPatient(UUID patientId, Pageable pageable);

    Page<AppointmentSummaryResponse> getByProvider(UUID providerId, Pageable pageable);

    List<AppointmentSummaryResponse> getTodaysAppointments(UUID providerId);

    Page<AppointmentSummaryResponse> getByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    AppointmentResponse confirm(UUID appointmentId);

    AppointmentResponse checkIn(UUID appointmentId, String checkInNotes);

    AppointmentResponse complete(UUID appointmentId, String completionNotes);

    AppointmentResponse cancel(UUID appointmentId, CancelAppointmentRequest request);

    AppointmentResponse markNoShow(UUID appointmentId);

    AppointmentResponse reschedule(UUID appointmentId, RescheduleAppointmentRequest request);

    boolean isSlotAvailable(UUID providerId, LocalDate date, java.time.LocalTime startTime, int durationMinutes);

    long countByDate(LocalDate date);

    long countByStatus(com.healthcare.appointment.domain.AppointmentStatus status);

    long countByStatusAndDateRange(com.healthcare.appointment.domain.AppointmentStatus status,
                                   LocalDate startDate, LocalDate endDate);

    long countByDateRange(LocalDate startDate, LocalDate endDate);

    List<AppointmentSummaryResponse> getUpcoming(int limit);
}
