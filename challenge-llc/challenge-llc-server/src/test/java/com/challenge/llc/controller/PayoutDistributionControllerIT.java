package com.challenge.llc.controller;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import com.challenge.llc.dto.request.DistributePayoutDto;
import com.challenge.llc.dto.response.PayoutDistributionDto;
import com.challenge.llc.dto.response.PersonPayoutDto;
import com.challenge.llc.server.testutils.AbstractServerIT;

@Slf4j
public class PayoutDistributionControllerIT extends AbstractServerIT {

    private static final String DISTRIBUTED_URL = "/v1/distribution/payout";

    @Test
    public void distributeRoundedValues() {
        Map<Integer, Map<String, PersonPayoutDto>> expectedDistribution = roundedValuesTestData();
        expectedDistribution
                .forEach((payout, distribution) -> runRoundedValues(BigDecimal.valueOf(payout), expectedDistribution));
    }

    private void runRoundedValues(
            BigDecimal payout,
            Map<Integer, Map<String, PersonPayoutDto>> expectedDistribution) {
        DistributePayoutDto distributePayout = DistributePayoutDto.builder()
                .payout(payout)
                .build();

        PayoutDistributionDto payoutDistribution = super.getTestRestTemplate()
                .postForObject(DISTRIBUTED_URL, distributePayout, PayoutDistributionDto.class);
        assertRoundedValues(expectedDistribution.get(payout.intValue()), payoutDistribution);
        assertThat(payoutDistribution.getLastPayout())
                .isEqualByComparingTo(payout);
    }

    private void assertRoundedValues(Map<String, PersonPayoutDto> expectedDistribution, PayoutDistributionDto payoutDistribution) {
        assertThat(payoutDistribution.getLeftOverSplit())
                .isEqualByComparingTo(BigDecimal.ZERO);
        Map<String, PersonPayoutDto> payoutPerPerson = payoutDistribution.getPersonPayouts()
                .stream()
                .collect(Collectors.toMap(PersonPayoutDto::getName, Function.identity()));
        payoutPerPerson
                .entrySet()
                .forEach(p -> assertRoundedValues(expectedDistribution, p));
    }

    private void assertRoundedValues(Map<String, PersonPayoutDto> expectedDistribution, Map.Entry<String, PersonPayoutDto> payoutDistribution) {
        String personName = payoutDistribution.getKey();
        PersonPayoutDto expectedPersonPayout = expectedDistribution.get(personName);
        assertThat(expectedPersonPayout)
                .withFailMessage(String.format("Expected Person with name %s should not be null.", personName))
                .isNotNull();

        PersonPayoutDto personPayout = payoutDistribution.getValue();

        assertThat(personPayout.getUuid()).isNotNull();

        BigDecimal expectedPayout = expectedPersonPayout.getPayout();
        BigDecimal payout = personPayout.getPayout();
        assertThat(payout)
                .withFailMessage(String.format("Expected Person with name %s Payout to be %s instead of %s.",
                        personName, expectedPayout, payout))
                .isNotNull()
                .isEqualByComparingTo(payout);
    }

    private Map<Integer, Map<String, PersonPayoutDto>> roundedValuesTestData() {
        return Map.of(
                100, this.roundedValuesTestData(80, 20, 0),
                200, this.roundedValuesTestData(120, 55, 25),
                300, this.roundedValuesTestData(170, 86.25, 43.75),
                400, this.roundedValuesTestData(220, 117.5, 62.5)
        );
    }

    private Map<String, PersonPayoutDto> roundedValuesTestData(
            double person1Payout,
            double person2Payout,
            double person3Payout
            ) {
        String person1Name = "Jessica";
        PersonPayoutDto person1 = PersonPayoutDto.builder()
                .payout(BigDecimal.valueOf(person1Payout))
                .name(person1Name)
                .build();

        String person2Name = "Henry";
        PersonPayoutDto person2 = PersonPayoutDto.builder()
                .payout(BigDecimal.valueOf(person2Payout))
                .name(person2Name)
                .build();

        String person3Name = "Lisa";
        PersonPayoutDto person3 = PersonPayoutDto.builder()
                .payout(BigDecimal.valueOf(person3Payout))
                .name(person3Name)
                .build();
        return Map.of(
                person1Name, person1,
                person2Name, person2,
                person3Name, person3
        );
    }
}
