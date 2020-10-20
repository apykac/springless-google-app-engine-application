package com.roundrobin_assignment.ticketpipeline.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roundrobin_assignment.ticketpipeline.config.context.Bean;
import com.roundrobin_assignment.ticketpipeline.config.context.Configuration;

@Configuration
public class MainConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
