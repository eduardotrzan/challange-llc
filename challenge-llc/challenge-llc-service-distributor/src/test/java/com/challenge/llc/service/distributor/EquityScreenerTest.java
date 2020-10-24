package com.challenge.llc.service.distributor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.challenge.llc.service.distributor.vo.DistributionEquitySummaryVo;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;
import com.challenge.llc.service.testutils.TestDataGenerator;

public class EquityScreenerTest {

    private EquityScreener equityScreener;

    @BeforeEach
    public void setup() {
        this.equityScreener = new EquityScreener();
    }

    @Test
    public void screenClassSummaryExceptions() {
        assertThatThrownBy(() -> this.equityScreener.screenEquityDistributionSummary(null, null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> this.equityScreener.screenEquityDistributionSummary(Collections.emptyList(), null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> this.equityScreener.screenEquityDistributionSummary(Collections.singletonList(null), null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> this.equityScreener
                .screenEquityDistributionSummary(Collections.singletonList(new PersonEquitySummaryVo()), ""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> this.equityScreener
                .screenEquityDistributionSummary(Collections.singletonList(new PersonEquitySummaryVo()), " "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void screenClassSummaryFindEquityClassNullEquityTypeOk() {
        List<PersonEquitySummaryVo> personEquitySummaries = TestDataGenerator.initialPersonEquitySummaries();
        DistributionEquitySummaryVo distributionEquitySummary = this.equityScreener.screenEquityDistributionSummary(personEquitySummaries, null);

        assertThat(distributionEquitySummary).isNotNull();

        int totalEquityType = personEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getEquityQuantity)
                .reduce(0, Integer::sum);
        assertThat(distributionEquitySummary.getTotalEquityType()).isNotNull().isEqualTo(totalEquityType);

        List<PersonEquitySummaryVo> screenedPersonEquitySummaries = distributionEquitySummary.getPersonEquitySummaries();
        assertThat(screenedPersonEquitySummaries).isNotNull().hasSize(personEquitySummaries.size());
        assertThat(screenedPersonEquitySummaries).containsExactlyInAnyOrderElementsOf(personEquitySummaries);
    }

    @Test
    public void screenClassSummaryFindEquityClassDifferentEquityTypeFromAllSetOk() {
        List<PersonEquitySummaryVo> personEquitySummaries = TestDataGenerator.initialPersonEquitySummaries();
        String equityType = String.format("EquityType-%s", System.nanoTime());
        DistributionEquitySummaryVo distributionEquitySummary = this.equityScreener.screenEquityDistributionSummary(personEquitySummaries, equityType);

        assertThat(distributionEquitySummary).isNotNull();

        assertThat(distributionEquitySummary.getTotalEquityType()).isNotNull().isZero();

        List<PersonEquitySummaryVo> screenedPersonEquitySummaries = distributionEquitySummary.getPersonEquitySummaries();
        assertThat(screenedPersonEquitySummaries).isNotNull().isEmpty();
    }

    @Test
    public void screenClassSummaryFindEquityClassXEquityTypeOk() {
        List<PersonEquitySummaryVo> personEquitySummaries = TestDataGenerator.initialPersonEquitySummaries();

        String equityType = "CLASS_X";
        DistributionEquitySummaryVo distributionEquitySummary = this.equityScreener.screenEquityDistributionSummary(personEquitySummaries, equityType);

        assertThat(distributionEquitySummary).isNotNull();

        List<PersonEquitySummaryVo> equityTypePersonSummaries = personEquitySummaries.stream()
                .filter(p -> p.getEquityType().equals(equityType))
                .collect(Collectors.toList());
        int totalEquityType = equityTypePersonSummaries
                .stream()
                .map(PersonEquitySummaryVo::getEquityQuantity)
                .reduce(0, Integer::sum);
        assertThat(distributionEquitySummary.getTotalEquityType()).isNotNull().isEqualTo(totalEquityType);

        List<PersonEquitySummaryVo> screenedPersonEquitySummaries = distributionEquitySummary.getPersonEquitySummaries();
        assertThat(screenedPersonEquitySummaries).isNotNull().hasSize(equityTypePersonSummaries.size());
        assertThat(screenedPersonEquitySummaries).containsExactlyInAnyOrderElementsOf(equityTypePersonSummaries);
    }

}
