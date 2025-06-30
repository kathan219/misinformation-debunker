package com.example.real_time.misinformation.debunker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.real_time.misinformation.debunker.model.PromptTemplate;
import com.example.real_time.misinformation.debunker.repository.ConfigRepository;

@Configuration
public class PromptConfig {

    private final ConfigRepository configRepository;

    public PromptConfig(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    
    @Bean
    public PromptTemplate promptTemplate() throws Exception {
        String template = configRepository.getConfigValue("FACTCHECK_PROMPT");
        
        if (template == null || template.isBlank()) {
            throw new IllegalStateException("Prompt template is empty in database");
        }
        
        if (!template.contains("%s")) {
            throw new IllegalStateException("Missing %s placeholder in template");
        }
        
        return new PromptTemplate(template);
    }
}