package com.challenge.llc.service.distributor;

import java.math.BigDecimal;
import java.util.List;

import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;

/**
 * Defines a recipe for creating Distributor Implementations that can swapped and customized.
 */
public interface IDistributor {

    /**
     * Distributes provided <b>payout</b> between the <b>personEquitySummaries</b>.
     *
     * @param personEquitySummaries People that will have payout split between
     * @param payout Amount of payout to be split
     * @return LeftOver after split is applied
     */
    BigDecimal distribute(List<PersonEquitySummaryVo> personEquitySummaries, BigDecimal payout);

}
