package com.challenge.llc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = { "lastPayout", "leftOverSplit", "personPayouts" })
public class PayoutDistributionDto {

    private BigDecimal lastPayout;

    private BigDecimal leftOverSplit;

    private List<PersonPayoutDto> personPayouts;

}
