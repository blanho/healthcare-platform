package com.healthcare.common.storage;

import java.io.InputStream;
import java.util.Optional;

public interface StorageService {

    StorageResult upload(StorageUploadRequest request);

    Optional<StorageObject> download(String bucketName, String objectKey);

    void delete(String bucketName, String objectKey);

    String generatePresignedUrl(String bucketName, String objectKey, int expirationMinutes);

    boolean objectExists(String bucketName, String objectKey);

    void createBucketIfNotExists(String bucketName);
}
