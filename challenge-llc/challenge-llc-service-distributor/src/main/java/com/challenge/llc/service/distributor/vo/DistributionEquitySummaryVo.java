package com.challenge.llc.service.distributor.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionEquitySummaryVo {

    private List<PersonEquitySummaryVo> personEquitySummaries;
    private Integer totalEquityType;

}
