package com.challenge.llc.service.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.llc.domain.entity.EquityPayout;
import com.challenge.llc.domain.entity.Person;
import com.challenge.llc.dto.response.PayoutDistributionDto;
import com.challenge.llc.dto.response.PersonPayoutDto;

@Component
public class PayoutDistributionBuilder {

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<PayoutDistributionDto> build(BigDecimal lastPayout, BigDecimal leftOverSplit, List<EquityPayout> equityPayouts) {
        if (!canCreateDistribution(lastPayout, equityPayouts)) {
            return Optional.empty();
        }

        var personPayouts = new HashMap<Long, PersonPayoutDto>();
        equityPayouts.forEach(e -> this.addPersonPayout(personPayouts, e));

        PayoutDistributionDto dto = PayoutDistributionDto.builder()
                .lastPayout(lastPayout)
                .leftOverSplit(leftOverSplit)
                .personPayouts(new ArrayList<>(personPayouts.values()))
                .build();
        return Optional.of(dto);
    }

    private boolean canCreateDistribution(BigDecimal lastPayout, List<EquityPayout> equityPayouts) {
        return CollectionUtils.isNotEmpty(equityPayouts)
                && lastPayout != null
                && lastPayout.compareTo(BigDecimal.ZERO) > 0;
    }

    private void addPersonPayout(Map<Long, PersonPayoutDto> personPayouts, EquityPayout equityPayout) {
        Person person = equityPayout.getEquity().getPerson();
        long personId = person.getId();

        PersonPayoutDto personPayout = personPayouts.get(personId);
        if (personPayout == null) {
            personPayout = PersonPayoutDto.builder()
                    .uuid(person.getUuid())
                    .name(person.getName())
                    .payout(BigDecimal.ZERO)
                    .build();
            personPayouts.put(personId, personPayout);
        }

        BigDecimal newPayout = personPayout.getPayout().add(equityPayout.getPayout());
        personPayout.setPayout(newPayout);
    }
}
