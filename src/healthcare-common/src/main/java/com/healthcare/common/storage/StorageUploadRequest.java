package com.healthcare.common.storage;

import java.io.InputStream;
import java.util.Map;

public record StorageUploadRequest(
        String bucketName,
        String objectKey,
        InputStream inputStream,
        long contentLength,
        String contentType,
        Map<String, String> metadata) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String bucketName;
        private String objectKey;
        private InputStream inputStream;
        private long contentLength;
        private String contentType = "application/octet-stream";
        private Map<String, String> metadata = Map.of();

        public Builder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public Builder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder contentLength(long contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public StorageUploadRequest build() {
            return new StorageUploadRequest(
                    bucketName, objectKey, inputStream, contentLength, contentType, metadata);
        }
    }
}
