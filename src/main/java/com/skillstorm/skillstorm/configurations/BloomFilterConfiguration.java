package com.skillstorm.skillstorm.configurations;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class BloomFilterConfiguration {

    @Bean
    public BloomFilter<String> usernameBloomFilter(){
        return BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8),10_000,0.01);
    }
}
