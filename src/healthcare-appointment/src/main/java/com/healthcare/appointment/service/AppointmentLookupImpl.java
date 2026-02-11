package com.healthcare.appointment.service;

import com.healthcare.appointment.api.AppointmentLookup;
import com.healthcare.appointment.domain.Appointment;
import com.healthcare.appointment.repository.AppointmentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class AppointmentLookupImpl implements AppointmentLookup {

    private final AppointmentRepository appointmentRepository;

    AppointmentLookupImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Optional<AppointmentInfo> findById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
            .map(this::toAppointmentInfo);
    }

    @Override
    public Optional<AppointmentInfo> findByAppointmentNumber(String appointmentNumber) {
        return appointmentRepository.findByAppointmentNumber(appointmentNumber)
            .map(this::toAppointmentInfo);
    }

    private AppointmentInfo toAppointmentInfo(Appointment appointment) {
        return new AppointmentInfo(
            appointment.getId(),
            appointment.getAppointmentNumber(),
            appointment.getPatientId(),
            appointment.getProviderId(),
            null,
            null,
            null,
            appointment.getTimeSlot().getStartDateTime().atZone(java.time.ZoneId.systemDefault()).toInstant(),
            appointment.getTimeSlot().getEndDateTime().atZone(java.time.ZoneId.systemDefault()).toInstant(),
            appointment.getStatus().name()
        );
    }
}
