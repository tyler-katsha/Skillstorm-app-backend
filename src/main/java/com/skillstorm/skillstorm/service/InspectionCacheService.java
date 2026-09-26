package com.skillstorm.skillstorm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InspectionCacheService {

    private final CacheManager cacheManager;

    public String inspectCache(String cacheName){
        Cache cache = cacheManager.getCache(cacheName);

        if(cache != null){
            return "Cache:\n" + Objects.requireNonNull(cache.getNativeCache());
        } else{
            return "No cache found with name: " + cacheName;
        }
    }
}
