package com.challenge.llc.service.distributor;

import java.math.BigDecimal;

import com.challenge.llc.service.distributor.vo.DistributionEquitySummaryVo;

/**
 * Defines a recipe for distributing payout to a group
 *
 */
public interface IEquityDistributor {

    /**
     * Distributes the payout among the group defined in summary of the equity distribution
     *
     * @param distributionEquitySummary summary of the equity distribution
     * @param payout amount to be distributed
     */
    void distributeEquity(DistributionEquitySummaryVo distributionEquitySummary, BigDecimal payout);

}
