package com.challenge.llc.service.testutils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;

public final class TestDataGenerator {

    private TestDataGenerator() { }

    public static List<PersonEquitySummaryVo> initialPersonEquitySummaries() {
        List<PersonEquitySummaryVo> personEquitySummaries = new ArrayList<>();
        personEquitySummaries.add(PersonEquitySummaryVo.builder()
                .personId(1)
                .personPayout(BigDecimal.ZERO)
                .equityType("CLASS_X")
                .equityQuantity(40)
                .build());
        personEquitySummaries.add(PersonEquitySummaryVo.builder()
                .personId(2)
                .personPayout(BigDecimal.ZERO)
                .equityType("CLASS_X")
                .equityQuantity(10)
                .build());
        personEquitySummaries.add(PersonEquitySummaryVo.builder()
                .personId(2)
                .personPayout(BigDecimal.ZERO)
                .equityType("CLASS_Y")
                .equityQuantity(15)
                .build());
        personEquitySummaries.add(PersonEquitySummaryVo.builder()
                .personId(3)
                .personPayout(BigDecimal.ZERO)
                .equityType("CLASS_Y")
                .equityQuantity(15)
                .build());

        return personEquitySummaries;
    }

    public static PersonEquitySummaryVo personEquitySummary(String equityType, Integer equityQuantity) {
        return PersonEquitySummaryVo.builder()
                .personId(randomIntegerBetween(1, 100))
                .personPayout(BigDecimal.ZERO)
                .equityType(equityType)
                .equityQuantity(equityQuantity)
                .build();
    }

    private static Integer randomIntegerBetween(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }
}
