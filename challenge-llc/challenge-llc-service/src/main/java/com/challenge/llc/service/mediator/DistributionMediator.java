package com.challenge.llc.service.mediator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.llc.domain.entity.Equity;
import com.challenge.llc.domain.entity.EquityPayout;
import com.challenge.llc.domain.entity.Person;
import com.challenge.llc.dto.response.CompanyDto;
import com.challenge.llc.dto.response.PayoutDistributionDto;
import com.challenge.llc.service.CompanyService;
import com.challenge.llc.service.DistributionService;
import com.challenge.llc.service.EquityPayoutService;
import com.challenge.llc.service.PersonService;
import com.challenge.llc.service.distributor.IDistributor;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;
import com.challenge.llc.service.mapper.CompanyMapper;
import com.challenge.llc.service.mapper.EquityPayoutMapper;
import com.challenge.llc.service.mapper.PayoutDistributionBuilder;
import com.challenge.llc.service.mapper.PersonMapper;
import com.challenge.llc.service.validation.ErrorCode;

@RequiredArgsConstructor
@Slf4j
@Service
public class DistributionMediator {

    private final PersonService personService;

    private final CompanyService companyService;

    private final EquityPayoutService equityPayoutService;

    private final DistributionService distributionService;

    private final CompanyMapper companyMapper;

    private final PersonMapper personMapper;

    private final EquityPayoutMapper equityPayoutMapper;

    private final PayoutDistributionBuilder payoutDistributionBuilder;

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Optional<CompanyDto> findDistributableCompanyByUuid(UUID companyUuid) {
        Validate.notNull(companyUuid);

        return this.companyService
                .findByUuid(companyUuid)
                .flatMap(this.companyMapper::toDto);
    }

    @Transactional
    public PayoutDistributionDto distribute(BigDecimal payout, UUID companyUuid) {
        Validate.notNull(payout);
        Validate.isTrue(payout.compareTo(BigDecimal.ZERO) > 0);

        UUID normalizedCompanyUuid = this.companyService.getDefaultCompanyIfNull(companyUuid);

        List<Person> persons = this.personService.findWithCompanyEquity(normalizedCompanyUuid);
        if (CollectionUtils.isEmpty(persons)) {
            throw ErrorCode.DISTRIBUTION_PERSON_GROUP.buildError(payout);
        }

        List<PersonEquitySummaryVo> personEquitySummaries = personMapper.toVos(persons, normalizedCompanyUuid);

        BigDecimal remainingChunkPayout = payout;
        List<IDistributor> distributors = this.distributionService.findDistributors(normalizedCompanyUuid);
        for (IDistributor distributor : distributors) {
            remainingChunkPayout = distributor.distribute(personEquitySummaries, remainingChunkPayout);
        }

        if (remainingChunkPayout.compareTo(BigDecimal.ZERO) != 0) {
            throw ErrorCode.UNBALANCED_CHUNK_DISTRIBUTION.buildError(remainingChunkPayout);
        }

        BigDecimal leftOverSplit = this.computeLeftoverSplitPayout(payout, personEquitySummaries);

        List<EquityPayout> newEquityPayouts = this.buildEquityPayouts(personEquitySummaries, persons);
        List<EquityPayout> savedEquityPayouts = this.equityPayoutService.save(newEquityPayouts);
        return this.payoutDistributionBuilder.build(payout, leftOverSplit, savedEquityPayouts)
                .orElseThrow(ErrorCode.BUILD_DISTRIBUTION::buildError);
    }

    private List<EquityPayout> buildEquityPayouts(List<PersonEquitySummaryVo> personEquitySummaries, List<Person> persons) {
        Map<Long, Equity> equities = persons
                .stream()
                .map(Person::getEquities)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Equity::getId, equity -> equity));

        return personEquitySummaries
                .stream()
                .map(e -> this.buildEquityPayout(e, equities))
                .collect(Collectors.toList());
    }

    private EquityPayout buildEquityPayout(PersonEquitySummaryVo personEquitySummary, Map<Long, Equity> equities) {
        EquityPayout equityPayout = this.equityPayoutMapper
                .toEntity(personEquitySummary)
                .orElseThrow(ErrorCode.MAPPING::buildError);

        Equity equity = equities.get(personEquitySummary.getEquityId());
        if (equity == null) {
            throw ErrorCode.NO_EQUITY.buildError();
        }

        equityPayout.setEquity(equity);
        return equityPayout;
    }

    private BigDecimal computeLeftoverSplitPayout(BigDecimal payout, List<PersonEquitySummaryVo> personEquitySummaries) {
        BigDecimal distributedPayout = personEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getPersonPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingPayout = payout.compareTo(distributedPayout) >= 0
                ? payout.subtract(distributedPayout)
                : payout;
        log.debug("payout={} less distributedPayout={} equals to remainingPayout={} ",
                payout, distributedPayout, remainingPayout);
        return remainingPayout;
    }

}
