package com.challenge.llc.service.distributor;

import java.util.List;

import com.challenge.llc.service.distributor.vo.DistributionEquitySummaryVo;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;

/**
 * Defines a recipe for screening a group based on their equity.
 *
 */
public interface IEquityScreener {

    /**
     * Screens out the <b>personEquitySummaries</b> based on <b>equityType</b>
     *
     * @param personEquitySummaries people that will be screened
     * @param equityType type of the equity to filter out
     * @return summary of the equity distribution
     */
    DistributionEquitySummaryVo screenEquityDistributionSummary(List<PersonEquitySummaryVo> personEquitySummaries, String equityType);

}
