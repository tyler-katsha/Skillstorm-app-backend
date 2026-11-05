package com.skillstorm.skillstorm.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.ast.tree.expression.Collation;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
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

    public boolean containsKey(String key) {
        return cache.getIfPresent(key) != null;
    }

    public <T> T getOrLoad(String key, Class<T> tClass, Supplier<T> fallback) {
        return get(key,tClass).orElseGet(() -> {
            T value = fallback.get();

            if(value != null) {
                put(key,value);
            }

            return value;
        });
    }

    public <T> List<T> getAll(String key, Class<T> tClass) {
        Object value = cache.getIfPresent(key);

        if(!(value instanceof Collection<?> collections)) {
            return Collections.emptyList();
        }

        return collections.stream()
                .filter(tClass::isInstance)
                .map(tClass::cast)
                .toList();
    }

    public <T> void putAll(String key,Collection<T> values) {
        if(values == null) return;

        cache.put(key,new ArrayList<>(values));
    }

    public void evict(String key){
        cache.invalidate(key);
    }
    public void evictAll(){
        cache.invalidateAll();
    }
}
