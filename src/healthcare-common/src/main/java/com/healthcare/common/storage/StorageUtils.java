package com.healthcare.common.storage;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public final class StorageUtils {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.of("UTC"));

    private StorageUtils() {}

    public static String generateObjectKey(String prefix, String originalFilename) {
        String datePath = DATE_FORMATTER.format(Instant.now());
        String uniqueId = UUID.randomUUID().toString();
        String extension = getFileExtension(originalFilename);

        return String.format("%s/%s/%s%s", prefix, datePath, uniqueId, extension);
    }

    public static String generatePatientDocumentKey(String patientId, String originalFilename) {
        return generateObjectKey("patients/" + patientId + "/documents", originalFilename);
    }

    public static String generateMedicalRecordAttachmentKey(
            String recordId, String originalFilename) {
        return generateObjectKey("medical-records/" + recordId + "/attachments", originalFilename);
    }

    public static String generateProviderDocumentKey(String providerId, String originalFilename) {
        return generateObjectKey("providers/" + providerId + "/documents", originalFilename);
    }

    public static String generateReportKey(String reportType, String originalFilename) {
        return generateObjectKey("reports/" + reportType, originalFilename);
    }

    public static String getFileExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDot).toLowerCase();
    }

    public static String getContentType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null ? contentType : "application/octet-stream";
    }

    public static boolean isAllowedFileType(String contentType, String... allowedTypes) {
        if (contentType == null || allowedTypes == null || allowedTypes.length == 0) {
            return false;
        }
        for (String allowed : allowedTypes) {
            if (contentType.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllowedImageType(String contentType) {
        return isAllowedFileType(
                contentType, "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml");
    }

    public static boolean isAllowedDocumentType(String contentType) {
        return isAllowedFileType(
                contentType,
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "text/plain",
                "text/csv");
    }

    public static void validateFileSize(long size, long maxSizeBytes) {
        if (size > maxSizeBytes) {
            throw new StorageException(
                    StorageException.StorageErrorCode.UNKNOWN_ERROR,
                    String.format(
                            "File size %d bytes exceeds maximum allowed size of %d bytes",
                            size, maxSizeBytes));
        }
    }

    public static void validateFileType(String contentType, String... allowedTypes) {
        if (!isAllowedFileType(contentType, allowedTypes)) {
            throw new StorageException(
                    StorageException.StorageErrorCode.UNKNOWN_ERROR,
                    "File type " + contentType + " is not allowed");
        }
    }
}
