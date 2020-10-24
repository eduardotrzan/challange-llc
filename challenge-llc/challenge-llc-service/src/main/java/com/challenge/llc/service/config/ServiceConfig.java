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

    /**
     * Double and Float suffer of <B>Cause of Loss of Significance</B> which means the binary can store only a closer approximation.
     * An example: 0.1 could be stored as 0.100000001490116119384765625
     *
     * Therefore the usage of BigDecimal that represents a signed decimal number of arbitrary precision with an associated scale.
     *
     * It's important to give preference to BigDecimal(String) instead of BigDecimal (double value), since the
     * <B>Cause of Loss of Significance</B> is carried over.
     * An example:
     * - BigDecimal (0.1) results in 0.1000000000000000055511151231257827021181583404541015625
     * - BigDecimal ("0.1") results in 0.1
     *
     *
     * <B>RoundingMode.HALF_EVEN</B> is know as Banker’s Rounding Mode which rounds to the nearest decimal, ties to even.
     * The problem for the distribution is that the rounding mode <B>RoundingMode.HALF_EVEN</B> would give a higher split
     * decimal.
     * Eg.: Assume a value of 0.0299999999999999988897769753748434595763683319091796875 the round mode would assume 0.03
     * in the split given an extra distribution of 1.110223025e-18.
     *
     * <B>RoundingMode.DOWN</B> would give a possible split and some left over.
     * The left over would be used according to each company's rules. Eg.: Carried over, split HALF_EVEN with company
     * paying the over difference, reinstate in company's finances
     *
     * https://dzone.com/articles/never-use-float-and-double-for-monetary-calculatio
     *
     * @return rounding mode for payout distribution
     */
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
