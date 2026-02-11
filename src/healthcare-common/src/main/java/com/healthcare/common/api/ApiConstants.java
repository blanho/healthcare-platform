package com.healthcare.common.api;

public final class ApiConstants {

    private ApiConstants() {

    }

    public static final String API_BASE = "/api";

    public static final String API_V1 = API_BASE + "/v1";

    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final int MAX_PAGE_SIZE = 100;

    public static final String DEFAULT_SORT_DIRECTION = "ASC";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
}
