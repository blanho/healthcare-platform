package com.healthcare.patient.exception;

import com.healthcare.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class DocumentNotFoundException extends ResourceNotFoundException {

    public DocumentNotFoundException(UUID documentId) {
        super("Document", "id", documentId.toString());
    }

    public DocumentNotFoundException(String objectKey) {
        super("Document", "objectKey", objectKey);
    }
}
