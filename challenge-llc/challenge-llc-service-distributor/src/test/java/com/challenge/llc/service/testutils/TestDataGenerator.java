package com.challenge.llc.service.testutils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;

public final class TestDataGenerator {

    private TestDataGenerator() { }

    public static List<PersonEquitySummaryVo> initialPersonEquitySummaries() {
        List<PersonEquitySummaryVo> personEquitySummaries = new ArrayList<>();
        personEquitySummaries.add(PersonEquitySummaryVo.builder()
                .personUuid(UUID.randomUUID())
                .personPayout(BigDecimal.ZERO)
                .equityType("CLASS_X")
                .equityQuantity(40)
                .build());

        UUID person2Uuid = UUID.randomUUID();
        personEquitySummaries.add(PersonEquitySummaryVo.builder()
                .personUuid(person2Uuid)
                .personPayout(BigDecimal.ZERO)
                .equityType("CLASS_X")
                .equityQuantity(10)
                .build());
        personEquitySummaries.add(PersonEquitySummaryVo.builder()
                .personUuid(person2Uuid)
                .personPayout(BigDecimal.ZERO)
                .equityType("CLASS_Y")
                .equityQuantity(15)
                .build());
        personEquitySummaries.add(PersonEquitySummaryVo.builder()
                .personUuid(UUID.randomUUID())
                .personPayout(BigDecimal.ZERO)
                .equityType("CLASS_Y")
                .equityQuantity(15)
                .build());

        return personEquitySummaries;
    }

    public static PersonEquitySummaryVo personEquitySummary(String equityType, Integer equityQuantity) {
        return PersonEquitySummaryVo.builder()
                .personUuid(UUID.randomUUID())
                .personPayout(BigDecimal.ZERO)
                .equityType(equityType)
                .equityQuantity(equityQuantity)
                .build();
    }
}
