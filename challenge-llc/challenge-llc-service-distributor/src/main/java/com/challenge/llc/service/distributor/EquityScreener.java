package com.challenge.llc.service.distributor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.challenge.llc.service.distributor.vo.DistributionEquitySummaryVo;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;

public class EquityScreener implements IEquityScreener {

    /**
     * Screens out the <b>personEquitySummaries</b> based on <b>equityType</b> where:
     *
     * IF there are no people with the type of equity provided THEN distribution summary is returned with no found people for distribution
     *
     * ELSE it will create a bucket with all the people having that particular equity type and the total of equity for that type
     *
     *
     * @param personEquitySummaries people that will be screened
     * @param equityType type of the equity to filter out
     * @return summary of the distribution
     */
    @Override
    public DistributionEquitySummaryVo screenEquityDistributionSummary(List<PersonEquitySummaryVo> personEquitySummaries, String equityType) {
        this.validate(personEquitySummaries, equityType);

        List<PersonEquitySummaryVo> screenedPersonEquitySummaries = this.screenByEquityType(personEquitySummaries, equityType);

        int totalEquityType = screenedPersonEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getEquityQuantity)
                .reduce(0, Integer::sum);

        return DistributionEquitySummaryVo.builder()
                .personEquitySummaries(screenedPersonEquitySummaries)
                .totalEquityType(totalEquityType)
                .build();
    }

    private void validate(List<PersonEquitySummaryVo> personEquitySummaries, String equityType) {
        Validate.notEmpty(
                personEquitySummaries,
                "PersonEquitySummaries is null or empty.");
        Validate.noNullElements(
                personEquitySummaries,
                "PersonEquitySummaries contains null elements.");
        Validate.isTrue(
                equityType == null || StringUtils.isNotBlank(equityType),
                "Equity type must not be a blank string.");
    }

    /**
     * Screens out <b>personEquitySummaries</b> based on the equity type.
     *
     * In case the equity type is null, <b>personEquitySummaries</b> will be returned as its original state.
     *
     * @param personEquitySummaries people that will be screened
     * @param equityType type of the equity to filter out
     * @return List of people screened out based on the equity tye provided
     */
    private List<PersonEquitySummaryVo> screenByEquityType(List<PersonEquitySummaryVo> personEquitySummaries, String equityType) {
        if (equityType == null) {
            return personEquitySummaries;
        }

        return Objects.requireNonNullElse(personEquitySummaries, Collections.<PersonEquitySummaryVo>emptyList())
                .stream()
                .filter(e -> e.getEquityType().equals(equityType))
                .collect(Collectors.toList());
    }
}
