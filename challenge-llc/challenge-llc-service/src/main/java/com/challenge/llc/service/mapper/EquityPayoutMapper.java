package com.challenge.llc.service.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.challenge.llc.domain.entity.EquityPayout;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;

@Component
public class EquityPayoutMapper {

    public Optional<EquityPayout> toEntity(PersonEquitySummaryVo vo) {
        if (vo == null) {
            return Optional.empty();
        }

        EquityPayout entity = EquityPayout.builder()
                .payout(vo.getPersonPayout())
                .build();
        return Optional.of(entity);
    }
}
