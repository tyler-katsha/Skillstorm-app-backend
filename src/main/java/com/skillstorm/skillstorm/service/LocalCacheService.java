package com.skillstorm.skillstorm.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class LocalCacheService {

    private final Cache<String,Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES) // deletes cache after 60 minutes
            .maximumSize(10_000)
            .build();

    public void put(String key,Object value){
        cache.put(key,value);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T>  get(String key,Class<T> tClass){
        Object value = cache.getIfPresent(key);

        if(tClass.isInstance(value)){
            return Optional.of((T) value);
        }
        return Optional.empty();
    }

    public void evict(String key){
        cache.invalidate(key);
    }
    public void evictAll(){
        cache.invalidateAll();
    }
}
