package com.challenge.llc.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.llc.domain.entity.EquitySplitRule;
import com.challenge.llc.domain.repo.interfaces.EquitySplitRuleRepository;
import com.challenge.llc.service.distributor.IDistributor;
import com.challenge.llc.service.mapper.DistributorBuilder;

@RequiredArgsConstructor
@Service
public class DistributionService {

    private final EquitySplitRuleRepository equitySplitRuleRepository;

    private final DistributorBuilder distributorBuilder;

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public List<IDistributor> findDistributors(UUID companyUuid) {
        Validate.notNull(companyUuid);

        List<EquitySplitRule> equitySplitRules = this.equitySplitRuleRepository.findByCompanyUuid(companyUuid);
        return this.distributorBuilder.buildDistributors(equitySplitRules);
    }

}
