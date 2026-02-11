package com.healthcare.common.storage;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnProperty(prefix = "healthcare.storage", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StorageConfig {

    private static final Logger log = LoggerFactory.getLogger(StorageConfig.class);

    @Bean
    public MinioClient minioClient(StorageProperties properties) {
        log.info("Configuring MinIO client with endpoint: {}", properties.getEndpoint());

        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .region(properties.getRegion())
                .build();
    }

    @Bean
    public StorageService storageService(MinioClient minioClient, StorageProperties properties) {
        log.info("Initializing MinIO storage service");
        return new MinioStorageService(minioClient, properties);
    }

    @Bean
    public StorageBucketInitializer storageBucketInitializer(
            StorageService storageService, StorageProperties properties) {
        return new StorageBucketInitializer(storageService, properties);
    }
}
