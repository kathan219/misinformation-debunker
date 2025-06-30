package com.example.real_time.misinformation.debunker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.real_time.misinformation.debunker.repository.ConfigRepository;

@Configuration
public class DatabaseConfig {

    @Bean
    public ConfigRepository configRepository(JdbcTemplate jdbcTemplate) {
        return new ConfigRepository(jdbcTemplate);
    }
}
