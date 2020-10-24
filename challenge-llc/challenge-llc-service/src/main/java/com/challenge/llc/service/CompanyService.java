package com.challenge.llc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.generic.domain.AbstractEntity;
import com.challenge.llc.domain.entity.Company;
import com.challenge.llc.domain.repo.interfaces.CompanyRepository;
import com.challenge.llc.service.validation.ErrorCode;

@RequiredArgsConstructor
@Slf4j
@Service
public class CompanyService {

    private static final String DEFAULT_COMPANY_NAME = "Krakatoa Ventures";

    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Optional<Company> findByUuid(UUID companyUuid) {
        Validate.notNull(companyUuid);

        return this.companyRepository
                .findByUuid(companyUuid);
    }


    /**
     * In the doc:
     * Requirements
     *
     * The program you write should work from the command line. It should:
     *
     * Read a single distribution amount e.g. 400
     * Write the payload to stdout
     *
     * ----
     *
     * - Ideally it should use UUID as a path param and payout as sub-resource
     * - Some sanitizer to avoid injects, keeping it simple
     * - Considering the constraints the challenge, will leave it null to fallback to 'Krakatoa Ventures'
     * @param companyUuid
     * @return
     */
    @Transactional(readOnly = true)
    public UUID getDefaultCompanyIfNull(UUID companyUuid) {
        if (companyUuid != null) {
            return companyUuid;
        }

        return this.companyRepository.findByName(DEFAULT_COMPANY_NAME)
                .map(AbstractEntity::getUuid)
                .orElseThrow(ErrorCode.NO_COMPANY::buildError);
    }

}
