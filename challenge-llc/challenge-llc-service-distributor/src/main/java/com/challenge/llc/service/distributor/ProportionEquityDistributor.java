package com.challenge.llc.service.distributor;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import com.challenge.llc.service.distributor.vo.DistributionEquitySummaryVo;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;

/**
 * Distributes proportionally the payout based on the amount of equity each shareholder has.
 * This is equivalent a Rule of Three.
 *
 * <B>Definition</B>
 * The Rule of Three Calculator uses the Rule of Three method to calculate the unknown value immediately based on
 * the proportion between two numbers and the third number.
 *
 */
@RequiredArgsConstructor
public class ProportionEquityDistributor implements IEquityDistributor {

    private final RoundingMode roundingMode;

    /**
     * Fore each <b>distributionEquitySummary</b> computes the distribution and sets to the person's payout
     *
     * @param distributionEquitySummary summary of the equity distribution
     * @param payout amount to be distributed
     */
    @Override
    public void distributeEquity(DistributionEquitySummaryVo distributionEquitySummary, BigDecimal payout) {
        this.validate(distributionEquitySummary, payout);
        Integer totalEquityType = distributionEquitySummary.getTotalEquityType();

        distributionEquitySummary
                .getPersonEquitySummaries()
                .forEach(d -> {
                    BigDecimal newPayout = this.computeAndDistributeEquity(d, payout, totalEquityType);
                    d.setPersonPayout(newPayout);
                });
    }

    private BigDecimal computeAndDistributeEquity(PersonEquitySummaryVo personEquitySummary, BigDecimal payout, int totalEquityType) {
        BigDecimal addingPayout = this.computeProportionally(payout, totalEquityType, personEquitySummary.getEquityQuantity());

        return Optional
                .ofNullable(personEquitySummary.getPersonPayout())
                .orElse(BigDecimal.ZERO)
                .add(addingPayout);
    }

    /**
     * Applies a Rule of Three on the payout to provide the person's proportion. Besides it applies a rounding mode to
     * the result.
     *
     * @param payout amount to be distributed
     * @param totalEquityType quantity of equity types
     * @param personEquityQuantity quantity of equities a person has
     * @return
     */
    private BigDecimal computeProportionally(BigDecimal payout, int totalEquityType, int personEquityQuantity) {
        return  payout
                .multiply(BigDecimal.valueOf(personEquityQuantity))
                .divide(BigDecimal.valueOf(totalEquityType))
                .setScale(2, this.roundingMode);
    }

    private void validate(DistributionEquitySummaryVo distributionEquitySummary, BigDecimal payout) {
        Validate.notNull(
                distributionEquitySummary,
                "Distribution Equity Summary must be provided as non-null.");
        Validate.notNull(
                distributionEquitySummary.getTotalEquityType(),
                "Total Equity must be provided as non-null and positive.");
        Validate.isTrue(
                distributionEquitySummary.getTotalEquityType() > 0,
                "Total Equity must be positive.");
        Validate.notEmpty(
                distributionEquitySummary.getPersonEquitySummaries(),
                "Cannot distribute to null PersonEquitySummaries.");
        Validate.noNullElements(
                distributionEquitySummary.getPersonEquitySummaries(),
                "Cannot distribute PersonEquitySummaries containing null elements.");
        Validate.notNull(
                payout,
                "Payout must be provided as non-null and positive.");
        Validate.isTrue(
                payout.compareTo(BigDecimal.ZERO) > 0,
                "Payout must be positive.");

    }
}
