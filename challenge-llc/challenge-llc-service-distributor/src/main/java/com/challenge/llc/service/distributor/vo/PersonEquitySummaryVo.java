package com.challenge.llc.service.distributor.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = { "personId", "personPayout", "equityId", "equityType", "equityQuantity" })
public class PersonEquitySummaryVo {

    private long personId;
    private BigDecimal personPayout;
    private long equityId;
    private String equityType;
    private int equityQuantity;
}
