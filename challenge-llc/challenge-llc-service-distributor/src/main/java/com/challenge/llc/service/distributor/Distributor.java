package com.challenge.llc.service.distributor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.challenge.llc.service.distributor.vo.DistributionEquitySummaryVo;
import com.challenge.llc.service.distributor.vo.DistributionRulesVo;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;

@Slf4j
@RequiredArgsConstructor
public class Distributor implements IDistributor {

    private final RoundingMode roundingMode;
    private final DistributionRulesVo distributionRules;
    private final IEquityScreener equityScreener;
    private final IEquityDistributor equityDistributor;

    /**
     * Apply distribution according to the defined {@link DistributionRulesVo#getChunkAmount()}
     * and following the rules {@link DistributionRulesVo#getSplitRules()}.
     *
     * <b>payout</b> will be normalized considering the chunk for the split and will follow the rules defined.
     * In case of <b>personEquitySummaries</b> and the normalized payout are not distributable, the LeftOver will the
     * same amount as the <b>payout</b>
     *
     * LeftOver: Depending on the method applied, a remaining amount could be left after the distribution method
     * has processed the shares and payout.
     * Classic cases like:
     * - Prime Numbers are hard to be divided, unless by it's own or multiples of it;
     * - Different Decimal precision can influence the distribution
     * - Rounding modes could crop unevenly
     *
     * @param personEquitySummaries People that will have payout split between
     * @param payout Amount of payout to be split
     * @return LeftOver after split is applied
     */
    @Override
    public BigDecimal distribute(List<PersonEquitySummaryVo> personEquitySummaries, BigDecimal payout) {
        this.logDistribution(personEquitySummaries, payout);
        BigDecimal distributionAmount = Optional
                .ofNullable(this.distributionRules.getChunkAmount())
                .filter(c -> payout.compareTo(c) > 0)
                .orElse(payout);

        if (!this.canDistribute(personEquitySummaries, distributionAmount)) {
            log.warn("Cannot distribute payout is null and/or personEquitySummaries is null or contains null elements.\n"
                            + "payout={} and personEquitySummaries={}",
                    payout, personEquitySummaries);
            return Optional
                    .ofNullable(payout)
                    .orElse(BigDecimal.ZERO);
        }

        this.distributePayout(personEquitySummaries, distributionAmount);

        BigDecimal remainingDistribution = payout
                .subtract(distributionAmount)
                .setScale(2, this.roundingMode);

        this.logDistribution(personEquitySummaries, remainingDistribution);
        return remainingDistribution;
    }

    private void logDistribution(List<PersonEquitySummaryVo> personEquitySummaries, BigDecimal payout) {
        List<Pair<Long, BigDecimal>> pairs = Objects
                .requireNonNullElse(personEquitySummaries, Collections.<PersonEquitySummaryVo>emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(p -> Pair.of(p.getPersonId(), p.getPersonPayout()))
                .collect(Collectors.toList());

        log.debug("Distribution personEquitySummaries={} and payout={}", pairs, payout);
    }

    private boolean canDistribute(List<PersonEquitySummaryVo> personEquitySummaries, BigDecimal payout) {
        return CollectionUtils.isNotEmpty(personEquitySummaries)
                && personEquitySummaries.stream().noneMatch(Objects::isNull)
                && payout != null
                && payout.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Orchestrate the distribution of payout between no split rules defined and provided split rules
     *
     * @param personEquitySummaries People that will have payout split between
     * @param distributionAmount Normalized payout of payout to be split
     */
    private void distributePayout(List<PersonEquitySummaryVo> personEquitySummaries, BigDecimal distributionAmount) {
        var splitRules = this.distributionRules.getSplitRules();
        if (MapUtils.isEmpty(splitRules)) {
            this.split(personEquitySummaries, null, distributionAmount);
            return;
        }

        this.splitWithRules(personEquitySummaries, distributionAmount, splitRules);
    }

    private void splitWithRules(List<PersonEquitySummaryVo> personEquitySummaries,
            BigDecimal distributionAmount,
            Map<String, Function<BigDecimal, BigDecimal>> splitRules) {
        splitRules.forEach((equityType, splitRule) -> {
            BigDecimal splitPayout = splitRule.apply(distributionAmount);
            this.split(personEquitySummaries, equityType, splitPayout);
        });
    }

    private void split(List<PersonEquitySummaryVo> personEquitySummaries, String equityType, BigDecimal distributionAmount) {
        DistributionEquitySummaryVo distributionEquitySummary = this.equityScreener
                .screenEquityDistributionSummary(personEquitySummaries, equityType);
        this.equityDistributor.distributeEquity(distributionEquitySummary, distributionAmount);
    }
}
