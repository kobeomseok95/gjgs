package com.gjgs.gjgs.infra.config.redis;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRedisRepositories //  Redis Repository 활성화
public class RedisConfig {

    // spring boot 2.0 이상에서는 해당 config가 yml로 인해 auto-configuration으로 자동으로 들어감
//    @Value("${spring.redis.port}")
//    private int port;
//
//    @Value("${spring.redis.host}")
//    private String host;
//
//
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(host, port);
//        return lettuceConnectionFactory;
//    }
//
//    @Bean
//    public RedisTemplate<?, ?> redisTemplate() {
//        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        return redisTemplate;
//    }

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues() // null value 캐시안함
                .entryTtl(Duration.ofSeconds(CacheKey.DEFAULT_EXPIRE_SEC)) // 캐시의 기본 유효시간 설정  -> 30분 // 무제한 주고 싶은게 있다면 이걸 설정하면 안된다.
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))

                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));


        // 캐시키별 default 유효시간 설정
        Map<String, RedisCacheConfiguration> cacheConfiguration = new HashMap<>();
        cacheConfiguration.put(CacheKey.ZONE,RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(CacheKey.ZONE_EXPIRE_SEC)));
        cacheConfiguration.put(CacheKey.CATEGORY,RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(CacheKey.CATEGORY_EXPIRE_SEC)));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(configuration)
                .withInitialCacheConfigurations(cacheConfiguration)
                .build();

    }
}