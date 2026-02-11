package com.healthcare.common.storage;

public class StorageException extends RuntimeException {

    private final StorageErrorCode errorCode;

    public StorageException(String message) {
        super(message);
        this.errorCode = StorageErrorCode.UNKNOWN_ERROR;
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = StorageErrorCode.UNKNOWN_ERROR;
    }

    public StorageException(StorageErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public StorageException(StorageErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public StorageErrorCode getErrorCode() {
        return errorCode;
    }

    public enum StorageErrorCode {
        BUCKET_NOT_FOUND,
        OBJECT_NOT_FOUND,
        ACCESS_DENIED,
        UPLOAD_FAILED,
        DOWNLOAD_FAILED,
        DELETE_FAILED,
        INVALID_BUCKET_NAME,
        INVALID_OBJECT_KEY,
        CONNECTION_ERROR,
        UNKNOWN_ERROR
    }
}
