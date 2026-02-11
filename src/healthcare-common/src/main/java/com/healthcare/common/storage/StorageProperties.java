package com.healthcare.common.storage;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "healthcare.storage")
public class StorageProperties {

    private boolean enabled = true;

    @NotBlank
    private String endpoint = "http://localhost:9000";

    @NotBlank
    private String accessKey;

    @NotBlank
    private String secretKey;

    private String region = "us-east-1";

    private int presignedUrlExpirationMinutes = 60;

    private Buckets buckets = new Buckets();

    @Getter
    @Setter
    public static class Buckets {
        private String documents = "healthcare-documents";
        private String images = "healthcare-images";
        private String reports = "healthcare-reports";
        private String attachments = "healthcare-attachments";
    }
}
