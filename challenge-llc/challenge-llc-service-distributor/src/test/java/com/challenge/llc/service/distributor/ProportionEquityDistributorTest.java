package com.challenge.llc.service.distributor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.challenge.llc.service.distributor.vo.DistributionEquitySummaryVo;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;
import com.challenge.llc.service.testutils.TestDataGenerator;

public class ProportionEquityDistributorTest {

    private ProportionEquityDistributor proportionEquityDistributor;

    @BeforeEach
    public void setup() {
        this.proportionEquityDistributor = new ProportionEquityDistributor(2, RoundingMode.DOWN);
    }

    @Test
    public void distributeEquityExceptions() {
        assertThatThrownBy(() -> this.proportionEquityDistributor.distributeEquity(null, null))
                .isInstanceOf(NullPointerException.class);

        DistributionEquitySummaryVo distributionEquitySummary = new DistributionEquitySummaryVo();
        assertThatThrownBy(() -> this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, null))
                .isInstanceOf(NullPointerException.class);

        distributionEquitySummary.setTotalEquityType(0);
        assertThatThrownBy(() -> this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, null))
                .isInstanceOf(IllegalArgumentException.class);

        distributionEquitySummary.setTotalEquityType(-1);
        assertThatThrownBy(() -> this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, null))
                .isInstanceOf(IllegalArgumentException.class);

        distributionEquitySummary.setTotalEquityType(10);
        assertThatThrownBy(() -> this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, null))
                .isInstanceOf(NullPointerException.class);

        distributionEquitySummary.setPersonEquitySummaries(Collections.emptyList());
        assertThatThrownBy(() -> this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, null))
                .isInstanceOf(IllegalArgumentException.class);

        distributionEquitySummary.setPersonEquitySummaries(Collections.singletonList(null));
        assertThatThrownBy(() -> this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, null))
                .isInstanceOf(IllegalArgumentException.class);

        distributionEquitySummary.setPersonEquitySummaries(Collections.singletonList(new PersonEquitySummaryVo()));
        assertThatThrownBy(() -> this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, BigDecimal.valueOf(-0.01)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void distributeEquityNoPreviousAmount() {
        String equityType = "CLASS_X";
        List<PersonEquitySummaryVo> personEquitySummaries = TestDataGenerator
                .initialPersonEquitySummaries()
                .stream()
                .filter(p -> p.getEquityType().equals(equityType))
                .collect(Collectors.toList());

        int totalEquityType = personEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getEquityQuantity)
                .reduce(0, Integer::sum);

        DistributionEquitySummaryVo distributionEquitySummary = DistributionEquitySummaryVo.builder()
                .totalEquityType(totalEquityType)
                .personEquitySummaries(personEquitySummaries)
                .build();

        BigDecimal payout = BigDecimal.valueOf(100);
        this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, payout);

        BigDecimal personPayout = personEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getPersonPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(personPayout).isNotZero().isEqualByComparingTo(payout);

        assertThat(personEquitySummaries.get(0).getPersonPayout())
                .isEqualByComparingTo(BigDecimal.valueOf(80));
        assertThat(personEquitySummaries.get(1).getPersonPayout())
                .isEqualByComparingTo(BigDecimal.valueOf(20));
    }

    @Test
    public void distributeEquityWithPreviousAmount200tTo300() {
        PersonEquitySummaryVo personEquitySummary1 = TestDataGenerator.personEquitySummary("ABC", 40);
        personEquitySummary1.setPersonPayout(BigDecimal.valueOf(120));

        PersonEquitySummaryVo personEquitySummary2 = TestDataGenerator.personEquitySummary("123", 25);
        personEquitySummary2.setPersonPayout(BigDecimal.valueOf(55));

        PersonEquitySummaryVo personEquitySummary3 = TestDataGenerator.personEquitySummary("E46", 15);
        personEquitySummary3.setPersonPayout(BigDecimal.valueOf(25));

        List<PersonEquitySummaryVo> personEquitySummaries = List.of(
                personEquitySummary1,
                personEquitySummary2,
                personEquitySummary3
        );
        BigDecimal alreadyDistributedPayout = personEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getPersonPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalEquityType = personEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getEquityQuantity)
                .reduce(0, Integer::sum);

        DistributionEquitySummaryVo distributionEquitySummary = DistributionEquitySummaryVo.builder()
                .totalEquityType(totalEquityType)
                .personEquitySummaries(personEquitySummaries)
                .build();

        BigDecimal payout = BigDecimal.valueOf(100);
        this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, payout);

        BigDecimal personPayout = personEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getPersonPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(personPayout)
                .isNotZero()
                .isEqualByComparingTo(alreadyDistributedPayout.add(payout));

        assertThat(personEquitySummary1.getPersonPayout())
                .isEqualByComparingTo(BigDecimal.valueOf(170));
        assertThat(personEquitySummary2.getPersonPayout())
                .isEqualByComparingTo(BigDecimal.valueOf(86.25));
        assertThat(personEquitySummary3.getPersonPayout())
                .isEqualByComparingTo(BigDecimal.valueOf(43.75));
    }



    @Test
    public void distributeEquityWithPreviousAmountFrom300tTo400() {
        PersonEquitySummaryVo personEquitySummary1 = TestDataGenerator.personEquitySummary("ABC", 40);
        personEquitySummary1.setPersonPayout(BigDecimal.valueOf(170));

        PersonEquitySummaryVo personEquitySummary2 = TestDataGenerator.personEquitySummary("123", 25);
        personEquitySummary2.setPersonPayout(BigDecimal.valueOf(86.25));

        PersonEquitySummaryVo personEquitySummary3 = TestDataGenerator.personEquitySummary("E46", 15);
        personEquitySummary3.setPersonPayout(BigDecimal.valueOf(43.75));

        List<PersonEquitySummaryVo> personEquitySummaries = List.of(
                personEquitySummary1,
                personEquitySummary2,
                personEquitySummary3
        );
        BigDecimal alreadyDistributedPayout = personEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getPersonPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalEquityType = personEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getEquityQuantity)
                .reduce(0, Integer::sum);

        DistributionEquitySummaryVo distributionEquitySummary = DistributionEquitySummaryVo.builder()
                .totalEquityType(totalEquityType)
                .personEquitySummaries(personEquitySummaries)
                .build();

        BigDecimal payout = BigDecimal.valueOf(100);
        this.proportionEquityDistributor.distributeEquity(distributionEquitySummary, payout);

        BigDecimal personPayout = personEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getPersonPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(personPayout)
                .isNotZero()
                .isEqualByComparingTo(alreadyDistributedPayout.add(payout));

        assertThat(personEquitySummary1.getPersonPayout())
                .isEqualByComparingTo(BigDecimal.valueOf(220));
        assertThat(personEquitySummary2.getPersonPayout())
                .isEqualByComparingTo(BigDecimal.valueOf(117.5));
        assertThat(personEquitySummary3.getPersonPayout())
                .isEqualByComparingTo(BigDecimal.valueOf(62.5));
    }
}
