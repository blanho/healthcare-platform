package com.healthcare.audit.service;

import java.lang.reflect.Field;
import java.util.Map;

public interface DataMaskingService {

    String mask(String value);

    String mask(String value, String pattern);

    Map<String, String> maskObject(Object object);

    Map<String, String> maskFields(Map<String, Object> data, String... fieldsToMask);

    boolean shouldMask(Field field);

    String maskChangeData(Map<String, Object[]> changes, Object entity);
}
