package com.healthcare.common.storage;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import java.io.InputStream;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinioStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);

    private final MinioClient minioClient;
    private final StorageProperties properties;

    public MinioStorageService(MinioClient minioClient, StorageProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public StorageResult upload(StorageUploadRequest request) {
        validateRequest(request);

        try {
            createBucketIfNotExists(request.bucketName());

            var putObjectArgs =
                    PutObjectArgs.builder()
                            .bucket(request.bucketName())
                            .object(request.objectKey())
                            .stream(request.inputStream(), request.contentLength(), -1)
                            .contentType(request.contentType())
                            .userMetadata(request.metadata())
                            .build();

            var response = minioClient.putObject(putObjectArgs);

            log.info(
                    "Uploaded object: bucket={}, key={}, etag={}",
                    request.bucketName(),
                    request.objectKey(),
                    response.etag());

            return StorageResult.builder()
                    .bucketName(request.bucketName())
                    .objectKey(request.objectKey())
                    .etag(response.etag())
                    .versionId(response.versionId())
                    .contentLength(request.contentLength())
                    .uploadedAt(Instant.now())
                    .build();

        } catch (Exception e) {
            log.error(
                    "Failed to upload object: bucket={}, key={}",
                    request.bucketName(),
                    request.objectKey(),
                    e);
            throw new StorageException(
                    StorageException.StorageErrorCode.UPLOAD_FAILED,
                    "Failed to upload object: " + request.objectKey(),
                    e);
        }
    }

    @Override
    public Optional<StorageObject> download(String bucketName, String objectKey) {
        validateBucketName(bucketName);
        validateObjectKey(objectKey);

        try {
            StatObjectResponse stat =
                    minioClient.statObject(
                            StatObjectArgs.builder().bucket(bucketName).object(objectKey).build());

            GetObjectResponse response =
                    minioClient.getObject(
                            GetObjectArgs.builder().bucket(bucketName).object(objectKey).build());

            return Optional.of(
                    StorageObject.builder()
                            .bucketName(bucketName)
                            .objectKey(objectKey)
                            .inputStream(response)
                            .contentLength(stat.size())
                            .contentType(stat.contentType())
                            .etag(stat.etag())
                            .metadata(stat.userMetadata())
                            .build());

        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return Optional.empty();
            }
            throw new StorageException(
                    StorageException.StorageErrorCode.DOWNLOAD_FAILED,
                    "Failed to download object: " + objectKey,
                    e);
        } catch (Exception e) {
            log.error("Failed to download object: bucket={}, key={}", bucketName, objectKey, e);
            throw new StorageException(
                    StorageException.StorageErrorCode.DOWNLOAD_FAILED,
                    "Failed to download object: " + objectKey,
                    e);
        }
    }

    @Override
    public void delete(String bucketName, String objectKey) {
        validateBucketName(bucketName);
        validateObjectKey(objectKey);

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build());

            log.info("Deleted object: bucket={}, key={}", bucketName, objectKey);

        } catch (Exception e) {
            log.error("Failed to delete object: bucket={}, key={}", bucketName, objectKey, e);
            throw new StorageException(
                    StorageException.StorageErrorCode.DELETE_FAILED,
                    "Failed to delete object: " + objectKey,
                    e);
        }
    }

    @Override
    public String generatePresignedUrl(String bucketName, String objectKey, int expirationMinutes) {
        validateBucketName(bucketName);
        validateObjectKey(objectKey);

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(expirationMinutes, TimeUnit.MINUTES)
                            .build());

        } catch (Exception e) {
            log.error(
                    "Failed to generate presigned URL: bucket={}, key={}", bucketName, objectKey, e);
            throw new StorageException(
                    StorageException.StorageErrorCode.UNKNOWN_ERROR,
                    "Failed to generate presigned URL for: " + objectKey,
                    e);
        }
    }

    @Override
    public boolean objectExists(String bucketName, String objectKey) {
        validateBucketName(bucketName);
        validateObjectKey(objectKey);

        try {
            minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(objectKey).build());
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return false;
            }
            throw new StorageException(
                    StorageException.StorageErrorCode.UNKNOWN_ERROR,
                    "Failed to check object existence: " + objectKey,
                    e);
        } catch (Exception e) {
            throw new StorageException(
                    StorageException.StorageErrorCode.CONNECTION_ERROR,
                    "Failed to check object existence: " + objectKey,
                    e);
        }
    }

    @Override
    public void createBucketIfNotExists(String bucketName) {
        validateBucketName(bucketName);

        try {
            boolean exists =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Created bucket: {}", bucketName);
            }

        } catch (Exception e) {
            log.error("Failed to create bucket: {}", bucketName, e);
            throw new StorageException(
                    StorageException.StorageErrorCode.UNKNOWN_ERROR,
                    "Failed to create bucket: " + bucketName,
                    e);
        }
    }

    private void validateRequest(StorageUploadRequest request) {
        if (request == null) {
            throw new StorageException(
                    StorageException.StorageErrorCode.UNKNOWN_ERROR, "Upload request cannot be null");
        }
        validateBucketName(request.bucketName());
        validateObjectKey(request.objectKey());
        if (request.inputStream() == null) {
            throw new StorageException(
                    StorageException.StorageErrorCode.UNKNOWN_ERROR, "Input stream cannot be null");
        }
    }

    private void validateBucketName(String bucketName) {
        if (bucketName == null || bucketName.isBlank()) {
            throw new StorageException(
                    StorageException.StorageErrorCode.INVALID_BUCKET_NAME,
                    "Bucket name cannot be empty");
        }
        if (bucketName.length() < 3 || bucketName.length() > 63) {
            throw new StorageException(
                    StorageException.StorageErrorCode.INVALID_BUCKET_NAME,
                    "Bucket name must be between 3 and 63 characters");
        }
    }

    private void validateObjectKey(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new StorageException(
                    StorageException.StorageErrorCode.INVALID_OBJECT_KEY,
                    "Object key cannot be empty");
        }
        if (objectKey.length() > 1024) {
            throw new StorageException(
                    StorageException.StorageErrorCode.INVALID_OBJECT_KEY,
                    "Object key cannot exceed 1024 characters");
        }
    }
}
