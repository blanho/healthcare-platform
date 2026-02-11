package com.healthcare.medicalrecord.exception;

import com.healthcare.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class MedicalRecordNotFoundException extends ResourceNotFoundException {

    private MedicalRecordNotFoundException(String identifier) {
        super("MedicalRecord", identifier);
    }

    private MedicalRecordNotFoundException(UUID id) {
        super("MedicalRecord", id);
    }

    public static MedicalRecordNotFoundException byId(UUID id) {
        return new MedicalRecordNotFoundException(id);
    }

    public static MedicalRecordNotFoundException byRecordNumber(String recordNumber) {
        return new MedicalRecordNotFoundException("record number: " + recordNumber);
    }
}
