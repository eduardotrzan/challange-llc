package com.challenge.llc.service.mapper;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.llc.domain.entity.EquitySplitRule;
import com.challenge.llc.domain.entity.SplitRule;
import com.challenge.llc.service.config.DefaultDistributionMode;
import com.challenge.llc.service.distributor.Distributor;
import com.challenge.llc.service.distributor.IEquityScreener;
import com.challenge.llc.service.distributor.IDistributor;
import com.challenge.llc.service.distributor.IEquityDistributor;
import com.challenge.llc.service.distributor.vo.DistributionRulesVo;

@RequiredArgsConstructor
@Component
public class DistributorBuilder {

    private final DefaultDistributionMode defaultDistributionMode;
    private final IEquityScreener equityScreener;
    private final IEquityDistributor equityDistributor;

    @Transactional(propagation = Propagation.MANDATORY)
    public List<IDistributor> buildDistributors(List<EquitySplitRule> equitySplitRules) {
        Validate.noNullElements(equitySplitRules);


        return equitySplitRules
                .stream()
                .sorted(Comparator.comparingLong(EquitySplitRule::getExecutionOrder))
                .map(this::buildDistributor)
                .collect(Collectors.toList());
    }

    private IDistributor buildDistributor(EquitySplitRule equitySplitRule) {
        DistributionRulesVo distributionRules = this.buildDistributionRules(equitySplitRule);
        return new Distributor(
                this.defaultDistributionMode.getScale(),
                this.defaultDistributionMode.getRoundingMode(),
                distributionRules,
                this.equityScreener,
                this.equityDistributor);
    }

    private DistributionRulesVo buildDistributionRules(EquitySplitRule equitySplitRule) {
        var splitRules = this.buildSplitRules(equitySplitRule.getSplitRules());
        return DistributionRulesVo.builder()
                .chunkAmount(equitySplitRule.getChunkAmount())
                .splitRules(splitRules)
                .build();
    }

    private Map<String, Function<BigDecimal, BigDecimal>> buildSplitRules(List<SplitRule> splitRules) {
        var splitRuleHandlers = new HashMap<String, Function<BigDecimal, BigDecimal>>();
        splitRules.forEach(s -> this.addSplitRule(splitRuleHandlers, s));
        return splitRuleHandlers;
    }

    private void addSplitRule(Map<String, Function<BigDecimal, BigDecimal>> splitRules, SplitRule splitRule) {
        String equityType = splitRule.getEquityType();
        var splitHandler = splitRules.get(equityType);
        if (splitHandler != null) {
            return;
        }

        splitHandler = (payout) -> payout.multiply(splitRule.getPercentageAllocated());
        splitRules.put(equityType, splitHandler);
    }
}
