package com.healthcare.common.constant;

public final class CommonConstants {

    private CommonConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    public static final String API_VERSION_V1 = "/api/v1";

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String FHIR_VERSION = "R4";
    public static final String HL7_VERSION = "2.5.1";

    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    public static final int MAX_NOTES_LENGTH = 2000;

    public static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;
    public static final String[] ALLOWED_FILE_TYPES = {"pdf", "jpg", "jpeg", "png", "doc", "docx"};
}
