package com.challenge.generic.server.filter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.challenge.generic.server.filter.RequestSessionIdFilter;

@Configuration
public class ServerFilterConfig {

    @Bean
    public RequestSessionIdFilter requestSessionIdFilter() {
        return new RequestSessionIdFilter();
    }
}
