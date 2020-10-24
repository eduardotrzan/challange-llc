package com.challenge.llc.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.challenge.llc.service.mediator.DistributionMediator;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayoutDistributionAuthEvaluator {

    private final DistributionMediator distributionMediator;

    public boolean canDistribute(UUID companyUuid) {
        return this.distributionMediator
                .findDistributableCompanyByUuid(companyUuid)
                .isPresent();
    }
}
