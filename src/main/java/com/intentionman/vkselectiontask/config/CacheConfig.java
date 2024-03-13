package com.intentionman.vkselectiontask.config;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheConfig implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(List.of("proxyCache"));
    }
}
