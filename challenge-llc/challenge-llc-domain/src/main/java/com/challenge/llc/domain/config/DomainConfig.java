package com.challenge.llc.domain.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = { "com.challenge.llc.domain.entity"})
@EnableJpaRepositories("com.challenge.llc.domain.repo")
public class DomainConfig {

}
