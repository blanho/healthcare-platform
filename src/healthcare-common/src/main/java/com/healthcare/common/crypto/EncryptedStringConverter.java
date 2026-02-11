package com.healthcare.common.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private static final Logger log = LoggerFactory.getLogger(EncryptedStringConverter.class);

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final String KEY_ALGORITHM = "AES";

    private static final String ENCRYPTION_KEY_ENV = "ENCRYPTION_KEY";
    private static final String DEV_KEY_WARNING = "DEVELOPMENT_KEY_DO_NOT_USE_IN_PRODUCTION";

    private final SecretKey secretKey;
    private final SecureRandom secureRandom;

    public EncryptedStringConverter() {
        this.secretKey = loadSecretKey();
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return attribute;
        }

        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedData = cipher.doFinal(attribute.getBytes());

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            log.error("Failed to encrypt attribute", e);
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return dbData;
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(dbData);

            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] encryptedData = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedData);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData);
        } catch (Exception e) {
            log.error("Failed to decrypt attribute", e);
            throw new IllegalStateException("Decryption failed", e);
        }
    }

    private SecretKey loadSecretKey() {
        String keyBase64 = System.getenv(ENCRYPTION_KEY_ENV);

        if (keyBase64 == null || keyBase64.isEmpty()) {

            log.warn("ENCRYPTION_KEY not set. Using development key. DO NOT USE IN PRODUCTION!");
            keyBase64 = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY=";
        } else if (keyBase64.contains(DEV_KEY_WARNING)) {
            throw new IllegalStateException(
                "Production encryption key not configured. Set ENCRYPTION_KEY environment variable.");
        }

        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            if (keyBytes.length != 32) {
                throw new IllegalStateException(
                    "ENCRYPTION_KEY must be exactly 32 bytes (256 bits). Got: " + keyBytes.length);
            }
            return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("ENCRYPTION_KEY must be valid Base64", e);
        }
    }
}
