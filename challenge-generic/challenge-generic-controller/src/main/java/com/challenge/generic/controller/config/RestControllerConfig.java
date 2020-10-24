package com.challenge.generic.controller.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.challenge.generic.controller.mapper.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@ComponentScan(basePackages = "com.challenge.generic.controller")
@Configuration
public class RestControllerConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return new JsonMapper();
    }

    @ConditionalOnMissingBean
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new JsonMapper();
    }

}
