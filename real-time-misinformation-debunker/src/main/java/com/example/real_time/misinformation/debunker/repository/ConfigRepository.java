package com.example.real_time.misinformation.debunker.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigRepository {

    private final JdbcTemplate jdbcTemplate;

    public ConfigRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getConfigValue(String key) throws Exception {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT config_value FROM app_config WHERE config_key = ?",
                String.class,
                key
            );
        } catch (EmptyResultDataAccessException e) {
            throw new Exception("Config key not found: " + key);
        }
    }
}


