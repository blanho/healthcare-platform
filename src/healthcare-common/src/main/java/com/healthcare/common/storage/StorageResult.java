package com.healthcare.common.storage;

import java.time.Instant;

public record StorageResult(
        String bucketName,
        String objectKey,
        String etag,
        String versionId,
        long contentLength,
        Instant uploadedAt) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String bucketName;
        private String objectKey;
        private String etag;
        private String versionId;
        private long contentLength;
        private Instant uploadedAt = Instant.now();

        public Builder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public Builder etag(String etag) {
            this.etag = etag;
            return this;
        }

        public Builder versionId(String versionId) {
            this.versionId = versionId;
            return this;
        }

        public Builder contentLength(long contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public Builder uploadedAt(Instant uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public StorageResult build() {
            return new StorageResult(
                    bucketName, objectKey, etag, versionId, contentLength, uploadedAt);
        }
    }
}
