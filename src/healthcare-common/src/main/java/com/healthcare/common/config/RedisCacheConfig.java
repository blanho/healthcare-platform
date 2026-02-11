package com.healthcare.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

    public static final String CACHE_PATIENTS = "patients";
    public static final String CACHE_PROVIDERS = "providers";
    public static final String CACHE_APPOINTMENTS = "appointments";
    public static final String CACHE_ROLES = "roles";
    public static final String CACHE_PERMISSIONS = "permissions";
    public static final String CACHE_USERS = "users";
    public static final String CACHE_REFERENCE_DATA = "referenceData";

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);
    private static final Duration REFERENCE_DATA_TTL = Duration.ofHours(1);
    private static final Duration USER_DATA_TTL = Duration.ofMinutes(5);
    private static final Duration SESSION_TTL = Duration.ofMinutes(15);

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        log.info("Creating custom RedisCacheManager with JDK serialization");

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(DEFAULT_TTL)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new JdkSerializationRedisSerializer()))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        cacheConfigs.put(CACHE_REFERENCE_DATA, defaultConfig.entryTtl(REFERENCE_DATA_TTL));
        cacheConfigs.put(CACHE_ROLES, defaultConfig.entryTtl(REFERENCE_DATA_TTL));
        cacheConfigs.put(CACHE_PERMISSIONS, defaultConfig.entryTtl(REFERENCE_DATA_TTL));

        cacheConfigs.put(CACHE_PATIENTS, defaultConfig.entryTtl(USER_DATA_TTL));
        cacheConfigs.put(CACHE_PROVIDERS, defaultConfig.entryTtl(USER_DATA_TTL));
        cacheConfigs.put(CACHE_USERS, defaultConfig.entryTtl(USER_DATA_TTL));

        cacheConfigs.put(CACHE_APPOINTMENTS, defaultConfig.entryTtl(Duration.ofMinutes(2)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .transactionAware()
                .build();
    }
}
