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
    @Autowired
    private CacheManager cacheManager;

    public void inspectCache(String cacheName){
        Cache cache = cacheManager.getCache(cacheName);

        if(cache != null){
            System.out.println("Cache: ");
            System.out.println(Objects.requireNonNull(cache.getNativeCache()));
        } else{
            System.out.println("No cache found with name: " + cacheName);
        }
    }
}
