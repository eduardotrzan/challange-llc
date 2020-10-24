package com.challenge.llc.service.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.RoundingMode;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultDistributionMode {

    private RoundingMode roundingMode;

    private int scale;

}
