package com.healthcare.audit.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.audit.domain.Phi;
import com.healthcare.audit.service.DataMaskingService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class DataMaskingServiceImpl implements DataMaskingService {

    private static final String DEFAULT_MASK = "***MASKED***";
    private static final Set<String> SENSITIVE_FIELD_NAMES = Set.of(
        "ssn", "socialSecurityNumber", "dateOfBirth", "dob", "birthDate",
        "address", "street", "city", "zip", "zipCode", "postalCode",
        "phone", "phoneNumber", "mobile", "email", "emailAddress",
        "firstName", "lastName", "fullName", "name",
        "medicalRecordNumber", "mrn", "patientId",
        "insuranceNumber", "policyNumber", "memberId",
        "diagnosis", "treatment", "medication", "prescription",
        "password", "secret", "token", "apiKey", "creditCard",
        "accountNumber", "routingNumber", "bankAccount"
    );

    private final ObjectMapper objectMapper;

    public DataMaskingServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String mask(String value) {
        return mask(value, DEFAULT_MASK);
    }

    @Override
    public String mask(String value, String pattern) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return pattern;
    }

    @Override
    public Map<String, String> maskObject(Object object) {
        if (object == null) {
            return Map.of();
        }

        Map<String, String> masked = new HashMap<>();
        Class<?> clazz = object.getClass();

        for (Field field : getAllFields(clazz)) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                String fieldName = field.getName();

                if (shouldMask(field)) {
                    masked.put(fieldName, DEFAULT_MASK);
                } else if (value != null) {
                    masked.put(fieldName, value.toString());
                }
            } catch (IllegalAccessException e) {

            }
        }

        return masked;
    }

    @Override
    public Map<String, String> maskFields(Map<String, Object> data, String... fieldsToMask) {
        Set<String> toMask = Set.of(fieldsToMask);
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (toMask.contains(key) || isSensitiveFieldName(key)) {
                result.put(key, DEFAULT_MASK);
            } else if (value != null) {
                result.put(key, value.toString());
            }
        }

        return result;
    }

    @Override
    public boolean shouldMask(Field field) {

        Phi phiAnnotation = field.getAnnotation(Phi.class);
        if (phiAnnotation != null && phiAnnotation.masked()) {
            return true;
        }

        return isSensitiveFieldName(field.getName());
    }

    @Override
    public String maskChangeData(Map<String, Object[]> changes, Object entity) {
        if (changes == null || changes.isEmpty()) {
            return null;
        }

        Map<String, Map<String, String>> maskedChanges = new HashMap<>();
        Class<?> clazz = entity != null ? entity.getClass() : null;

        for (Map.Entry<String, Object[]> entry : changes.entrySet()) {
            String fieldName = entry.getKey();
            Object[] values = entry.getValue();

            boolean shouldMask = false;

            if (clazz != null) {
                try {
                    Field field = findField(clazz, fieldName);
                    if (field != null) {
                        shouldMask = shouldMask(field);
                    }
                } catch (Exception e) {

                    shouldMask = isSensitiveFieldName(fieldName);
                }
            } else {
                shouldMask = isSensitiveFieldName(fieldName);
            }

            Map<String, String> change = new HashMap<>();
            if (shouldMask) {
                change.put("old", DEFAULT_MASK);
                change.put("new", DEFAULT_MASK);
            } else {
                change.put("old", values[0] != null ? values[0].toString() : null);
                change.put("new", values[1] != null ? values[1].toString() : null);
            }
            maskedChanges.put(fieldName, change);
        }

        try {
            return objectMapper.writeValueAsString(maskedChanges);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private boolean isSensitiveFieldName(String fieldName) {
        if (fieldName == null) {
            return false;
        }
        String lowerName = fieldName.toLowerCase();
        return SENSITIVE_FIELD_NAMES.stream()
            .anyMatch(sensitive -> lowerName.contains(sensitive.toLowerCase()));
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
