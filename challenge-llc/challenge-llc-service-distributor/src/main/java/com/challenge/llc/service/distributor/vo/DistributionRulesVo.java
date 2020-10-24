package com.challenge.llc.service.distributor.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionRulesVo {

    private BigDecimal chunkAmount;

    private Map<String, Function<BigDecimal, BigDecimal>> splitRules;
}
