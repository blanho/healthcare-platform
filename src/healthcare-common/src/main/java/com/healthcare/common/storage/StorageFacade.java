package com.healthcare.common.storage;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class StorageFacade {

    private static final long MAX_FILE_SIZE_BYTES = 50 * 1024 * 1024;

    private final StorageService storageService;
    private final StorageProperties properties;

    public StorageFacade(StorageService storageService, StorageProperties properties) {
        this.storageService = storageService;
        this.properties = properties;
    }

    public StorageResult uploadPatientDocument(
            String patientId, MultipartFile file, Map<String, String> metadata) {
        validateFile(file);
        String objectKey =
                StorageUtils.generatePatientDocumentKey(patientId, file.getOriginalFilename());
        return uploadFile(properties.getBuckets().getDocuments(), objectKey, file, metadata);
    }

    public StorageResult uploadMedicalRecordAttachment(
            String recordId, MultipartFile file, Map<String, String> metadata) {
        validateFile(file);
        String objectKey =
                StorageUtils.generateMedicalRecordAttachmentKey(recordId, file.getOriginalFilename());
        return uploadFile(properties.getBuckets().getAttachments(), objectKey, file, metadata);
    }

    public StorageResult uploadImage(String prefix, MultipartFile file, Map<String, String> metadata) {
        validateImageFile(file);
        String objectKey = StorageUtils.generateObjectKey(prefix, file.getOriginalFilename());
        return uploadFile(properties.getBuckets().getImages(), objectKey, file, metadata);
    }

    public StorageResult uploadReport(
            String reportType, MultipartFile file, Map<String, String> metadata) {
        validateFile(file);
        String objectKey = StorageUtils.generateReportKey(reportType, file.getOriginalFilename());
        return uploadFile(properties.getBuckets().getReports(), objectKey, file, metadata);
    }

    public Optional<StorageObject> downloadDocument(String objectKey) {
        return storageService.download(properties.getBuckets().getDocuments(), objectKey);
    }

    public Optional<StorageObject> downloadAttachment(String objectKey) {
        return storageService.download(properties.getBuckets().getAttachments(), objectKey);
    }

    public Optional<StorageObject> downloadImage(String objectKey) {
        return storageService.download(properties.getBuckets().getImages(), objectKey);
    }

    public Optional<StorageObject> downloadReport(String objectKey) {
        return storageService.download(properties.getBuckets().getReports(), objectKey);
    }

    public String getDocumentPresignedUrl(String objectKey) {
        return storageService.generatePresignedUrl(
                properties.getBuckets().getDocuments(),
                objectKey,
                properties.getPresignedUrlExpirationMinutes());
    }

    public String getAttachmentPresignedUrl(String objectKey) {
        return storageService.generatePresignedUrl(
                properties.getBuckets().getAttachments(),
                objectKey,
                properties.getPresignedUrlExpirationMinutes());
    }

    public String getImagePresignedUrl(String objectKey) {
        return storageService.generatePresignedUrl(
                properties.getBuckets().getImages(),
                objectKey,
                properties.getPresignedUrlExpirationMinutes());
    }

    public void deleteDocument(String objectKey) {
        storageService.delete(properties.getBuckets().getDocuments(), objectKey);
    }

    public void deleteAttachment(String objectKey) {
        storageService.delete(properties.getBuckets().getAttachments(), objectKey);
    }

    public void deleteImage(String objectKey) {
        storageService.delete(properties.getBuckets().getImages(), objectKey);
    }

    private StorageResult uploadFile(
            String bucketName, String objectKey, MultipartFile file, Map<String, String> metadata) {
        try {
            InputStream inputStream = file.getInputStream();
            StorageUploadRequest request =
                    StorageUploadRequest.builder()
                            .bucketName(bucketName)
                            .objectKey(objectKey)
                            .inputStream(inputStream)
                            .contentLength(file.getSize())
                            .contentType(StorageUtils.getContentType(file))
                            .metadata(metadata != null ? metadata : Map.of())
                            .build();
            return storageService.upload(request);
        } catch (Exception e) {
            throw new StorageException(
                    StorageException.StorageErrorCode.UPLOAD_FAILED,
                    "Failed to upload file: " + file.getOriginalFilename(),
                    e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException(
                    StorageException.StorageErrorCode.UNKNOWN_ERROR, "File cannot be empty");
        }
        StorageUtils.validateFileSize(file.getSize(), MAX_FILE_SIZE_BYTES);
    }

    private void validateImageFile(MultipartFile file) {
        validateFile(file);
        if (!StorageUtils.isAllowedImageType(file.getContentType())) {
            throw new StorageException(
                    StorageException.StorageErrorCode.UNKNOWN_ERROR,
                    "Invalid image type: " + file.getContentType());
        }
    }
}
