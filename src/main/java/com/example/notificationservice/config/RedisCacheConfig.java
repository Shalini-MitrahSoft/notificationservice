package com.example.notificationservice.config;

import com.example.notificationservice.dto.NotificationResponse;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     ObjectMapper objectMapper,
                                     @Value("${spring.cache.redis.time-to-live:10m}") Duration ttl) {
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues();

        JacksonJsonRedisSerializer<NotificationResponse> notificationSerializer =
                new JacksonJsonRedisSerializer<>(objectMapper, NotificationResponse.class);
        JacksonJsonRedisSerializer<List<NotificationResponse>> notificationListSerializer =
                new JacksonJsonRedisSerializer<>(objectMapper,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, NotificationResponse.class));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(Map.of(
                        "notification", typed(base, notificationSerializer),
                        "notificationList", typed(base, notificationListSerializer),
                        "customerNotifications", typed(base, notificationListSerializer)))
                .build();
    }

    private <T> RedisCacheConfiguration typed(RedisCacheConfiguration base,
                                                JacksonJsonRedisSerializer<T> serializer) {
        return base.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }
}
