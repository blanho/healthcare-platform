package com.healthcare.common.storage;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageBucketInitializer {

    private static final Logger log = LoggerFactory.getLogger(StorageBucketInitializer.class);

    private final StorageService storageService;
    private final StorageProperties properties;

    public StorageBucketInitializer(StorageService storageService, StorageProperties properties) {
        this.storageService = storageService;
        this.properties = properties;
    }

    @PostConstruct
    public void initializeBuckets() {
        log.info("Initializing storage buckets...");

        var buckets = properties.getBuckets();
        createBucketSafely(buckets.getDocuments());
        createBucketSafely(buckets.getImages());
        createBucketSafely(buckets.getReports());
        createBucketSafely(buckets.getAttachments());

        log.info("Storage buckets initialized successfully");
    }

    private void createBucketSafely(String bucketName) {
        try {
            storageService.createBucketIfNotExists(bucketName);
            log.debug("Bucket ready: {}", bucketName);
        } catch (StorageException e) {
            log.warn("Failed to create bucket {}: {}", bucketName, e.getMessage());
        }
    }
}
