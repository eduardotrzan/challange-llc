package com.challenge.llc.service.config;

import java.math.RoundingMode;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.challenge.llc.domain.config.DomainConfig;
import com.challenge.llc.service.distributor.EquityScreener;
import com.challenge.llc.service.distributor.IEquityScreener;
import com.challenge.llc.service.distributor.IEquityDistributor;
import com.challenge.llc.service.distributor.ProportionEquityDistributor;

@Configuration
@ComponentScan(basePackages = "com.challenge.llc.service")
@Import({ DomainConfig.class })
public class ServiceConfig {

    @Bean
    public RoundingMode distributionRoundMode() {
        return RoundingMode.DOWN;
    }

    @Bean
    public IEquityScreener equityScreener() {
        return new EquityScreener();
    }

    @Bean
    public IEquityDistributor equityDistributor(RoundingMode distributionRoundMode) {
        return new ProportionEquityDistributor(distributionRoundMode);
    }

}
