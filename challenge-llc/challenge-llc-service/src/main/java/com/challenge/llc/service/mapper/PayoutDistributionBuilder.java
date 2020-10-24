package com.challenge.llc.service.mapper;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.challenge.llc.dto.response.PayoutDistributionDto;
import com.challenge.llc.dto.response.PersonPayoutDto;
import com.challenge.llc.service.config.DefaultDistributionMode;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;

@RequiredArgsConstructor
@Component
public class PayoutDistributionBuilder {

    private final DefaultDistributionMode defaultDistributionMode;

    public Optional<PayoutDistributionDto> build(BigDecimal lastPayout, BigDecimal leftOverSplit, List<PersonEquitySummaryVo> personEquitySummaries) {
        if (!canCreateDistribution(lastPayout, personEquitySummaries)) {
            return Optional.empty();
        }

        var personPayouts = new HashMap<UUID, PersonPayoutDto>();
        personEquitySummaries.forEach(e -> this.addPersonPayout(personPayouts, e));

        PayoutDistributionDto dto = PayoutDistributionDto.builder()
                .lastPayout(lastPayout)
                .leftOverSplit(leftOverSplit)
                .personPayouts(new ArrayList<>(personPayouts.values()))
                .build();
        return Optional.of(dto);
    }

    private boolean canCreateDistribution(BigDecimal lastPayout, List<PersonEquitySummaryVo> personEquitySummaries) {
        return CollectionUtils.isNotEmpty(personEquitySummaries)
                && lastPayout != null
                && lastPayout.compareTo(BigDecimal.ZERO) > 0;
    }

    private void addPersonPayout(Map<UUID, PersonPayoutDto> personPayouts, PersonEquitySummaryVo personEquitySummary) {
        UUID personUuid = personEquitySummary.getPersonUuid();

        PersonPayoutDto personPayout = personPayouts.get(personUuid);
        if (personPayout == null) {
            personPayout = PersonPayoutDto.builder()
                    .uuid(personUuid)
                    .name(personEquitySummary.getPersonName())
                    .payout(BigDecimal.ZERO)
                    .build();
            personPayouts.put(personUuid, personPayout);
        }

        BigDecimal personPayoutAmount = personEquitySummary.getPersonPayout();

        int scale = this.defaultDistributionMode.getScale();
        RoundingMode roundingMode = this.defaultDistributionMode.getRoundingMode();

        BigDecimal newPayout = personPayout
                .getPayout()
                .add(personPayoutAmount)
                .setScale(scale, roundingMode);
        personPayout.setPayout(newPayout);
    }
}
