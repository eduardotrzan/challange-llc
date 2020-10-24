package com.challenge.llc.service.distributor.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = { "personUuid", "personPayout", "equityId", "equityType", "equityQuantity" })
public class PersonEquitySummaryVo {

    private String personName;
    private UUID personUuid;
    private BigDecimal personPayout;
    private long equityId;
    private String equityType;
    private int equityQuantity;
}
