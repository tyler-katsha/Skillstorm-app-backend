package com.skillstorm.skillstorm.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class InspectionCacheService {

    private final CacheManager cacheManager;

    public String inspectCache(String cacheName){
        org.springframework.cache.Cache springCache = cacheManager.getCache(cacheName);

        if(springCache != null){

            Object nativeCache = springCache.getNativeCache();

            if(nativeCache instanceof Cache ){

                @SuppressWarnings("unchecked")
                Cache<Object,Object> caffineCache = (Cache<Object,Object>) nativeCache;

                ConcurrentMap<Object,Object> cacheData = caffineCache.asMap();

                return "Cache contents for '" + cacheName + "':\n" + cacheData;
            } else {
                return "Cache is not a Caffeine cache. Native type: " + nativeCache.getClass().getName();
            }
        } else{
            return "No cache found with name: " + cacheName;
        }
    }
}
