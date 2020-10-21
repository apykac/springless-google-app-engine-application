package com.ticket_pipeline.simple_exchange.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket_pipeline.simple_context.Bean;
import com.ticket_pipeline.simple_context.Configuration;

@Configuration
public class MainConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
