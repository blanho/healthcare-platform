package com.healthcare.medicalrecord.service;

import com.healthcare.medicalrecord.api.dto.*;
import com.healthcare.medicalrecord.domain.*;
import com.healthcare.medicalrecord.exception.InvalidRecordOperationException;
import com.healthcare.medicalrecord.exception.MedicalRecordNotFoundException;
import com.healthcare.medicalrecord.repository.MedicalRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordServiceImpl.class);

    private final MedicalRecordRepository medicalRecordRepository;
    private final RecordNumberGenerator recordNumberGenerator;
    private final ApplicationEventPublisher eventPublisher;

    public MedicalRecordServiceImpl(
            MedicalRecordRepository medicalRecordRepository,
            RecordNumberGenerator recordNumberGenerator,
            ApplicationEventPublisher eventPublisher) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.recordNumberGenerator = recordNumberGenerator;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public MedicalRecordResponse create(CreateMedicalRecordRequest request) {
        log.info("Creating medical record for patient {} by provider {}",
                request.patientId(), request.providerId());

        String recordNumber = recordNumberGenerator.generate();

        MedicalRecord record = MedicalRecord.create(
                recordNumber,
                request.patientId(),
                request.providerId(),
                request.appointmentId(),
                request.recordType(),
                request.recordDate(),
                request.chiefComplaint()
        );

        record.setNotes(request.notes());

        if (request.vitalSigns() != null) {
            record.recordVitals(mapToVitalSigns(request.vitalSigns()));
        }

        if (request.soapNote() != null) {
            record.updateSoapNote(mapToSoapNote(request.soapNote()));
        }

        if (request.diagnoses() != null) {
            request.diagnoses().forEach(d -> record.addDiagnosis(mapToDiagnosis(d)));
        }

        MedicalRecord saved = medicalRecordRepository.save(record);
        publishEvents(saved);

        log.info("Medical record created: {}", saved.getRecordNumber());
        return mapToResponse(saved);
    }

    @Override
    public MedicalRecordResponse update(UUID recordId, UpdateMedicalRecordRequest request) {
        log.info("Updating medical record: {}", recordId);

        MedicalRecord record = findById(recordId);

        if (!record.isEditable()) {
            throw InvalidRecordOperationException.cannotEdit(record.getStatus().name());
        }

        if (request.chiefComplaint() != null) {
            record.setChiefComplaint(request.chiefComplaint());
        }
        if (request.notes() != null) {
            record.setNotes(request.notes());
        }
        if (request.vitalSigns() != null) {
            record.recordVitals(mapToVitalSigns(request.vitalSigns()));
        }
        if (request.soapNote() != null) {
            record.updateSoapNote(mapToSoapNote(request.soapNote()));
        }
        if (request.diagnoses() != null) {
            record.clearDiagnoses();
            request.diagnoses().forEach(d -> record.addDiagnosis(mapToDiagnosis(d)));
        }

        MedicalRecord saved = medicalRecordRepository.save(record);
        publishEvents(saved);

        log.info("Medical record updated: {}", saved.getRecordNumber());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponse getById(UUID recordId) {
        log.debug("Retrieving medical record: {}", recordId);

        MedicalRecord record = findById(recordId);
        return mapToResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponse getByRecordNumber(String recordNumber) {
        log.debug("Retrieving medical record by number: {}", recordNumber);
        MedicalRecord record = medicalRecordRepository.findByRecordNumber(recordNumber)
                .orElseThrow(() -> MedicalRecordNotFoundException.byRecordNumber(recordNumber));
        return mapToResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordSummaryResponse> search(MedicalRecordSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching medical records with criteria");
        Specification<MedicalRecord> spec = buildSpecification(criteria);
        return medicalRecordRepository.findAll(spec, pageable).map(this::mapToSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordSummaryResponse> getByPatient(UUID patientId, Pageable pageable) {
        log.debug("Retrieving medical records for patient: {}", patientId);
        return medicalRecordRepository.findByPatientId(patientId, pageable).map(this::mapToSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordSummaryResponse> getByProvider(UUID providerId, Pageable pageable) {
        log.debug("Retrieving medical records for provider: {}", providerId);
        return medicalRecordRepository.findByProviderId(providerId, pageable).map(this::mapToSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordSummaryResponse> getByAppointment(UUID appointmentId) {
        log.debug("Retrieving medical records for appointment: {}", appointmentId);
        return medicalRecordRepository.findByAppointmentIdAndDeletedFalse(appointmentId)
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordSummaryResponse> getDraftRecords(UUID providerId) {
        log.debug("Retrieving draft records for provider: {}", providerId);
        return medicalRecordRepository.findDraftRecordsByProvider(providerId)
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    @Override
    public MedicalRecordResponse finalize(UUID recordId, String userId) {
        log.info("Finalizing medical record: {} by user: {}", recordId, userId);

        MedicalRecord record = findById(recordId);

        if (!record.getStatus().canFinalize()) {
            throw InvalidRecordOperationException.cannotFinalize(record.getStatus().name());
        }

        record.finalize(userId);

        MedicalRecord saved = medicalRecordRepository.save(record);
        publishEvents(saved);

        log.info("Medical record finalized: {}", saved.getRecordNumber());
        return mapToResponse(saved);
    }

    @Override
    public MedicalRecordResponse amend(UUID recordId, AmendRecordRequest request, String userId) {
        log.info("Amending medical record: {} by user: {}", recordId, userId);

        MedicalRecord record = findById(recordId);

        if (!record.getStatus().canAmend()) {
            throw InvalidRecordOperationException.cannotAmend(record.getStatus().name());
        }

        record.amend(request.reason(), userId);

        if (request.additionalNotes() != null) {
            String existingNotes = record.getNotes() != null ? record.getNotes() + "\n\n" : "";
            record.setNotes(existingNotes + "AMENDMENT (" + Instant.now() + "): " + request.additionalNotes());
        }

        MedicalRecord saved = medicalRecordRepository.save(record);
        publishEvents(saved);

        log.info("Medical record amended: {}", saved.getRecordNumber());
        return mapToResponse(saved);
    }

    @Override
    public MedicalRecordResponse voidRecord(UUID recordId, VoidRecordRequest request, String userId) {
        log.info("Voiding medical record: {} by user: {}", recordId, userId);

        MedicalRecord record = findById(recordId);

        if (!record.getStatus().canVoid()) {
            throw InvalidRecordOperationException.cannotVoid(record.getStatus().name());
        }

        record.voidRecord(request.reason(), userId);

        MedicalRecord saved = medicalRecordRepository.save(record);
        publishEvents(saved);

        log.info("Medical record voided: {}", saved.getRecordNumber());
        return mapToResponse(saved);
    }

    @Override
    public MedicalRecordResponse addVitals(UUID recordId, VitalSignsRequest request) {
        log.info("Adding vital signs to medical record: {}", recordId);

        MedicalRecord record = findById(recordId);
        record.recordVitals(mapToVitalSigns(request));

        MedicalRecord saved = medicalRecordRepository.save(record);
        publishEvents(saved);

        return mapToResponse(saved);
    }

    @Override
    public MedicalRecordResponse updateSoapNote(UUID recordId, SoapNoteRequest request) {
        log.info("Updating SOAP note for medical record: {}", recordId);

        MedicalRecord record = findById(recordId);
        record.updateSoapNote(mapToSoapNote(request));

        MedicalRecord saved = medicalRecordRepository.save(record);
        return mapToResponse(saved);
    }

    @Override
    public MedicalRecordResponse addDiagnosis(UUID recordId, DiagnosisRequest request) {
        log.info("Adding diagnosis to medical record: {}", recordId);

        MedicalRecord record = findById(recordId);
        record.addDiagnosis(mapToDiagnosis(request));

        MedicalRecord saved = medicalRecordRepository.save(record);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordSummaryResponse> getPatientTimeline(UUID patientId, int limit) {
        log.debug("Retrieving patient timeline for: {}", patientId);
        return medicalRecordRepository.findRecentByPatientId(patientId, PageRequest.of(0, limit))
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    private MedicalRecord findById(UUID recordId) {
        return medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> MedicalRecordNotFoundException.byId(recordId));
    }

    private void publishEvents(MedicalRecord record) {
        record.getDomainEvents().forEach(eventPublisher::publishEvent);
        record.clearDomainEvents();
    }

    private VitalSigns mapToVitalSigns(VitalSignsRequest request) {
        return VitalSigns.builder()
                .systolicBp(request.systolicBp())
                .diastolicBp(request.diastolicBp())
                .heartRate(request.heartRate())
                .respiratoryRate(request.respiratoryRate())
                .temperature(request.temperature())
                .oxygenSaturation(request.oxygenSaturation())
                .weightKg(request.weightKg())
                .heightCm(request.heightCm())
                .painLevel(request.painLevel())
                .recordedAt(request.recordedAt() != null ? request.recordedAt() : Instant.now())
                .build();
    }

    private SoapNote mapToSoapNote(SoapNoteRequest request) {
        return SoapNote.builder()
                .subjective(request.subjective())
                .objective(request.objective())
                .assessment(request.assessment())
                .plan(request.plan())
                .build();
    }

    private Diagnosis mapToDiagnosis(DiagnosisRequest request) {
        return Diagnosis.builder()
                .code(request.code().toUpperCase())
                .description(request.description())
                .type(request.type())
                .primary(request.primary())
                .onsetDate(request.onsetDate())
                .resolvedDate(request.resolvedDate())
                .notes(request.notes())
                .build();
    }

    private Specification<MedicalRecord> buildSpecification(MedicalRecordSearchCriteria criteria) {
        return Specification.where(patientIdEquals(criteria.patientId()))
                .and(providerIdEquals(criteria.providerId()))
                .and(appointmentIdEquals(criteria.appointmentId()))
                .and(recordTypeEquals(criteria.recordType()))
                .and(statusEquals(criteria.status()))
                .and(dateAfterOrEquals(criteria.startDate()))
                .and(dateBeforeOrEquals(criteria.endDate()))
                .and(notDeleted());
    }

    private Specification<MedicalRecord> patientIdEquals(UUID patientId) {
        return (root, query, cb) -> patientId == null ? null :
                cb.equal(root.get("patientId"), patientId);
    }

    private Specification<MedicalRecord> providerIdEquals(UUID providerId) {
        return (root, query, cb) -> providerId == null ? null :
                cb.equal(root.get("providerId"), providerId);
    }

    private Specification<MedicalRecord> appointmentIdEquals(UUID appointmentId) {
        return (root, query, cb) -> appointmentId == null ? null :
                cb.equal(root.get("appointmentId"), appointmentId);
    }

    private Specification<MedicalRecord> recordTypeEquals(RecordType recordType) {
        return (root, query, cb) -> recordType == null ? null :
                cb.equal(root.get("recordType"), recordType);
    }

    private Specification<MedicalRecord> statusEquals(RecordStatus status) {
        return (root, query, cb) -> status == null ? null :
                cb.equal(root.get("status"), status);
    }

    private Specification<MedicalRecord> dateAfterOrEquals(java.time.LocalDateTime startDate) {
        return (root, query, cb) -> startDate == null ? null :
                cb.greaterThanOrEqualTo(root.get("recordDate"), startDate);
    }

    private Specification<MedicalRecord> dateBeforeOrEquals(java.time.LocalDateTime endDate) {
        return (root, query, cb) -> endDate == null ? null :
                cb.lessThanOrEqualTo(root.get("recordDate"), endDate);
    }

    private Specification<MedicalRecord> notDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    private MedicalRecordResponse mapToResponse(MedicalRecord record) {
        MedicalRecordResponse.VitalSignsResponse vitalsResponse = null;
        if (record.getVitalSigns() != null) {
            VitalSigns v = record.getVitalSigns();
            vitalsResponse = new MedicalRecordResponse.VitalSignsResponse(
                    v.getSystolicBp(),
                    v.getDiastolicBp(),
                    v.getBloodPressure(),
                    v.getHeartRate(),
                    v.getRespiratoryRate(),
                    v.getTemperature(),
                    v.getOxygenSaturation(),
                    v.getWeightKg(),
                    v.getHeightCm(),
                    v.calculateBmi(),
                    v.getPainLevel(),
                    v.getRecordedAt(),
                    v.hasCriticalValue()
            );
        }

        MedicalRecordResponse.SoapNoteResponse soapResponse = null;
        if (record.getSoapNote() != null) {
            SoapNote s = record.getSoapNote();
            soapResponse = new MedicalRecordResponse.SoapNoteResponse(
                    s.getSubjective(),
                    s.getObjective(),
                    s.getAssessment(),
                    s.getPlan(),
                    s.isComplete()
            );
        }

        List<MedicalRecordResponse.DiagnosisResponse> diagnosesResponse = record.getDiagnoses().stream()
                .map(d -> new MedicalRecordResponse.DiagnosisResponse(
                        d.getCode(),
                        d.getDescription(),
                        d.getType(),
                        d.isPrimary(),
                        d.getOnsetDate(),
                        d.getResolvedDate(),
                        d.getNotes(),
                        d.isResolved(),
                        d.isChronic()
                ))
                .toList();

        return new MedicalRecordResponse(
                record.getId(),
                record.getRecordNumber(),
                record.getPatientId(),
                record.getProviderId(),
                record.getAppointmentId(),
                record.getRecordType(),
                record.getRecordDate(),
                record.getChiefComplaint(),
                record.getNotes(),
                vitalsResponse,
                soapResponse,
                diagnosesResponse,
                record.getStatus(),
                record.getFinalizedAt(),
                record.getFinalizedBy(),
                record.getAttachmentsCount(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }

    private MedicalRecordSummaryResponse mapToSummary(MedicalRecord record) {
        Diagnosis primary = record.getPrimaryDiagnosis();
        return new MedicalRecordSummaryResponse(
                record.getId(),
                record.getRecordNumber(),
                record.getPatientId(),
                record.getProviderId(),
                record.getRecordType(),
                record.getRecordDate(),
                record.getChiefComplaint(),
                primary != null ? primary.getCode() : null,
                primary != null ? primary.getDescription() : null,
                record.getStatus(),
                record.getAttachmentsCount(),
                record.getCreatedAt()
        );
    }
}
