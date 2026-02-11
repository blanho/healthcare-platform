package com.healthcare.appointment.service;

import com.healthcare.appointment.api.dto.*;
import com.healthcare.appointment.domain.Appointment;
import com.healthcare.appointment.domain.AppointmentStatus;
import com.healthcare.appointment.domain.AppointmentType;
import com.healthcare.appointment.domain.TimeSlot;
import com.healthcare.appointment.exception.AppointmentNotFoundException;
import com.healthcare.appointment.exception.InvalidAppointmentStateException;
import com.healthcare.appointment.exception.TimeSlotConflictException;
import com.healthcare.appointment.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;
    private final AppointmentNumberGenerator numberGenerator;
    private final ApplicationEventPublisher eventPublisher;

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            AppointmentNumberGenerator numberGenerator,
            ApplicationEventPublisher eventPublisher) {
        this.appointmentRepository = appointmentRepository;
        this.numberGenerator = numberGenerator;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public AppointmentResponse schedule(ScheduleAppointmentRequest request) {
        log.info("Scheduling appointment for patient {} with provider {} on {}",
                request.patientId(), request.providerId(), request.scheduledDate());

        validateSlotAvailability(
                request.providerId(),
                request.scheduledDate(),
                request.startTime(),
                request.durationMinutes()
        );

        TimeSlot timeSlot = TimeSlot.of(
                request.scheduledDate(),
                request.startTime(),
                request.durationMinutes()
        );

        String appointmentNumber = numberGenerator.generate();

        Appointment appointment = Appointment.schedule(
                appointmentNumber,
                request.patientId(),
                request.providerId(),
                timeSlot,
                request.appointmentType(),
                request.reasonForVisit(),
                request.notes()
        );

        Appointment saved = appointmentRepository.save(appointment);

        saved.getDomainEvents().forEach(eventPublisher::publishEvent);
        saved.clearDomainEvents();

        log.info("Appointment scheduled successfully: {}", saved.getAppointmentNumber());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getById(UUID appointmentId) {
        Appointment appointment = findById(appointmentId);
        return mapToResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getByAppointmentNumber(String appointmentNumber) {
        Appointment appointment = appointmentRepository.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> AppointmentNotFoundException.byAppointmentNumber(appointmentNumber));
        return mapToResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentSummaryResponse> search(AppointmentSearchCriteria criteria, Pageable pageable) {
        Specification<Appointment> spec = buildSpecification(criteria);
        return appointmentRepository.findAll(spec, pageable).map(this::mapToSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentSummaryResponse> getByPatient(UUID patientId, Pageable pageable) {
        return appointmentRepository.findByPatientIdOrderByDateDesc(patientId, pageable)
                .map(this::mapToSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentSummaryResponse> getByProvider(UUID providerId, Pageable pageable) {
        return appointmentRepository.findByProviderIdOrderByDateDesc(providerId, pageable)
                .map(this::mapToSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentSummaryResponse> getTodaysAppointments(UUID providerId) {
        return appointmentRepository.findTodaysAppointments(providerId, LocalDate.now())
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentSummaryResponse> getByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return appointmentRepository.findByDateRange(startDate, endDate, pageable)
                .map(this::mapToSummary);
    }

    @Override
    public AppointmentResponse confirm(UUID appointmentId) {
        log.info("Confirming appointment: {}", appointmentId);

        Appointment appointment = findById(appointmentId);
        appointment.confirm();

        Appointment saved = appointmentRepository.save(appointment);
        publishEvents(saved);

        log.info("Appointment confirmed: {}", saved.getAppointmentNumber());
        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse checkIn(UUID appointmentId, String checkInNotes) {
        log.info("Checking in appointment: {}", appointmentId);

        Appointment appointment = findById(appointmentId);

        if (!appointment.getStatus().canCheckIn()) {
            throw InvalidAppointmentStateException.cannotTransition(
                    appointment.getStatus().name(),
                    "check-in"
            );
        }

        appointment.checkIn(checkInNotes);

        Appointment saved = appointmentRepository.save(appointment);
        publishEvents(saved);

        log.info("Patient checked in for appointment: {}", saved.getAppointmentNumber());
        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse complete(UUID appointmentId, String completionNotes) {
        log.info("Completing appointment: {}", appointmentId);

        Appointment appointment = findById(appointmentId);

        if (!appointment.getStatus().equals(AppointmentStatus.CHECKED_IN)) {
            throw InvalidAppointmentStateException.cannotTransition(
                    appointment.getStatus().name(),
                    "complete"
            );
        }

        appointment.complete(completionNotes);

        Appointment saved = appointmentRepository.save(appointment);
        publishEvents(saved);

        log.info("Appointment completed: {}", saved.getAppointmentNumber());
        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse cancel(UUID appointmentId, CancelAppointmentRequest request) {
        log.info("Cancelling appointment: {}", appointmentId);

        Appointment appointment = findById(appointmentId);

        if (!appointment.getStatus().canCancel()) {
            throw InvalidAppointmentStateException.cannotTransition(
                    appointment.getStatus().name(),
                    "cancel"
            );
        }

        appointment.cancel(request.reason(), request.cancelledByPatient());

        Appointment saved = appointmentRepository.save(appointment);
        publishEvents(saved);

        log.info("Appointment cancelled: {}", saved.getAppointmentNumber());
        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse markNoShow(UUID appointmentId) {
        log.info("Marking appointment as no-show: {}", appointmentId);

        Appointment appointment = findById(appointmentId);
        appointment.markNoShow();

        Appointment saved = appointmentRepository.save(appointment);
        publishEvents(saved);

        log.info("Appointment marked as no-show: {}", saved.getAppointmentNumber());
        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse reschedule(UUID appointmentId, RescheduleAppointmentRequest request) {
        log.info("Rescheduling appointment {} to {}", appointmentId, request.newDate());

        Appointment appointment = findById(appointmentId);

        if (!appointment.getStatus().canReschedule()) {
            throw InvalidAppointmentStateException.cannotTransition(
                    appointment.getStatus().name(),
                    "reschedule"
            );
        }

        List<Appointment> conflicts = appointmentRepository.findOverlappingAppointments(
                appointment.getProviderId(),
                request.newDate(),
                request.newStartTime(),
                request.newStartTime().plusMinutes(request.durationMinutes())
        );

        conflicts = conflicts.stream()
                .filter(a -> !a.getId().equals(appointmentId))
                .toList();

        if (!conflicts.isEmpty()) {
            throw TimeSlotConflictException.forProvider(
                    appointment.getProviderId(),
                    request.newDate(),
                    request.newStartTime()
            );
        }

        TimeSlot newTimeSlot = TimeSlot.of(
                request.newDate(),
                request.newStartTime(),
                request.durationMinutes()
        );

        appointment.reschedule(newTimeSlot);

        Appointment saved = appointmentRepository.save(appointment);
        publishEvents(saved);

        log.info("Appointment rescheduled: {} to {}",
                saved.getAppointmentNumber(), newTimeSlot.getScheduledDate());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSlotAvailable(UUID providerId, LocalDate date, LocalTime startTime, int durationMinutes) {
        LocalTime endTime = startTime.plusMinutes(durationMinutes);
        List<Appointment> conflicts = appointmentRepository.findOverlappingAppointments(
                providerId, date, startTime, endTime);
        return conflicts.isEmpty();
    }

    private Appointment findById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> AppointmentNotFoundException.byId(appointmentId));
    }

    private void validateSlotAvailability(UUID providerId, LocalDate date, LocalTime startTime, int durationMinutes) {
        if (!isSlotAvailable(providerId, date, startTime, durationMinutes)) {
            throw TimeSlotConflictException.forProvider(providerId, date, startTime);
        }
    }

    private void publishEvents(Appointment appointment) {
        appointment.getDomainEvents().forEach(eventPublisher::publishEvent);
        appointment.clearDomainEvents();
    }

    private Specification<Appointment> buildSpecification(AppointmentSearchCriteria criteria) {
        return Specification.where(patientIdEquals(criteria.patientId()))
                .and(providerIdEquals(criteria.providerId()))
                .and(appointmentTypeEquals(criteria.appointmentType()))
                .and(statusEquals(criteria.status()))
                .and(dateAfterOrEquals(criteria.startDate()))
                .and(dateBeforeOrEquals(criteria.endDate()));
    }

    private Specification<Appointment> patientIdEquals(UUID patientId) {
        return (root, query, cb) -> patientId == null ? null :
                cb.equal(root.get("patientId"), patientId);
    }

    private Specification<Appointment> providerIdEquals(UUID providerId) {
        return (root, query, cb) -> providerId == null ? null :
                cb.equal(root.get("providerId"), providerId);
    }

    private Specification<Appointment> appointmentTypeEquals(AppointmentType type) {
        return (root, query, cb) -> type == null ? null :
                cb.equal(root.get("appointmentType"), type);
    }

    private Specification<Appointment> statusEquals(AppointmentStatus status) {
        return (root, query, cb) -> status == null ? null :
                cb.equal(root.get("status"), status);
    }

    private Specification<Appointment> dateAfterOrEquals(LocalDate startDate) {
        return (root, query, cb) -> startDate == null ? null :
                cb.greaterThanOrEqualTo(root.get("timeSlot").get("date"), startDate);
    }

    private Specification<Appointment> dateBeforeOrEquals(LocalDate endDate) {
        return (root, query, cb) -> endDate == null ? null :
                cb.lessThanOrEqualTo(root.get("timeSlot").get("date"), endDate);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        AppointmentResponse.CancellationInfo cancellationInfo = null;
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            cancellationInfo = new AppointmentResponse.CancellationInfo(
                    appointment.getCancellationReason(),
                    appointment.getCancelledAt(),
                    appointment.isCancelledByPatient()
            );
        }

        AppointmentResponse.CheckInInfo checkInInfo = null;
        if (appointment.getCheckedInAt() != null) {
            checkInInfo = new AppointmentResponse.CheckInInfo(
                    appointment.getCheckedInAt(),
                    appointment.getCheckInNotes()
            );
        }

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getAppointmentNumber(),
                appointment.getPatientId(),
                appointment.getProviderId(),
                appointment.getTimeSlot().getScheduledDate(),
                appointment.getTimeSlot().getStartTime(),
                appointment.getTimeSlot().getEndTime(),
                appointment.getTimeSlot().getDurationMinutes(),
                appointment.getAppointmentType(),
                appointment.getStatus(),
                appointment.getReasonForVisit(),
                appointment.getNotes(),
                cancellationInfo,
                checkInInfo,
                appointment.getCompletedAt(),
                appointment.getCompletionNotes(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }

    private AppointmentSummaryResponse mapToSummary(Appointment appointment) {
        return new AppointmentSummaryResponse(
                appointment.getId(),
                appointment.getAppointmentNumber(),
                appointment.getPatientId(),
                appointment.getProviderId(),
                appointment.getTimeSlot().getScheduledDate(),
                appointment.getTimeSlot().getStartTime(),
                appointment.getTimeSlot().getEndTime(),
                appointment.getAppointmentType(),
                appointment.getStatus()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long countByDate(LocalDate date) {
        return appointmentRepository.countByTimeSlotScheduledDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(AppointmentStatus status) {
        return appointmentRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatusAndDateRange(AppointmentStatus status, LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.countByStatusAndTimeSlotScheduledDateBetween(status, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByDateRange(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.countByTimeSlotScheduledDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentSummaryResponse> getUpcoming(int limit) {
        LocalDate today = LocalDate.now();
        return appointmentRepository.findByTimeSlotScheduledDateGreaterThanEqualAndStatusInOrderByTimeSlotScheduledDateAscTimeSlotStartTimeAsc(
                today,
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED),
                org.springframework.data.domain.PageRequest.of(0, limit)
        ).stream().map(this::mapToSummary).toList();
    }
}
