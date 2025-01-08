package com.hello.helloworld.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@SuppressWarnings("unused")
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(27, TimeUnit.MINUTES);
    }

    @Bean(name = "cacheManager")
    @Primary

    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        caffeineCacheManager.setCacheNames(List.of(""));
        return caffeineCacheManager;
    }
}
