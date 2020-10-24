package com.challenge.llc.service;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.llc.domain.entity.EquityPayout;
import com.challenge.llc.domain.repo.interfaces.EquityPayoutRepository;

@RequiredArgsConstructor
@Service
public class EquityPayoutService {

    private final EquityPayoutRepository equityPayoutRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public List<EquityPayout> save(List<EquityPayout> equityPayouts) {
        Validate.notEmpty(equityPayouts);
        Validate.noNullElements(equityPayouts);

        return this.equityPayoutRepository.saveAll(equityPayouts);
    }

}
