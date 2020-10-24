package com.challenge.llc.service.distributor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.challenge.llc.service.distributor.vo.DistributionEquitySummaryVo;
import com.challenge.llc.service.distributor.vo.DistributionRulesVo;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;
import com.challenge.llc.service.testutils.TestDataGenerator;

@ExtendWith(MockitoExtension.class)
public class DistributorTest {

    @Mock
    private DistributionRulesVo distributionRules;

    @Mock
    private IEquityScreener equityScreener;

    @Mock
    private IEquityDistributor equityDistributor;

    private Distributor distributor;

    @BeforeEach
    public void setup() {
        this.distributor = new Distributor(
                RoundingMode.DOWN,
                this.distributionRules,
                this.equityScreener,
                this.equityDistributor
        );
    }

    @Test
    public void distributeNullOrEmptyPersonEquitySummaryReturnsNormalizedPayout() {
        BigDecimal payout = null;
        BigDecimal remainingChunk = this.distributor.distribute(null, payout);
        assertThat(remainingChunk).isNotNull().isEqualTo(BigDecimal.ZERO);

        remainingChunk = this.distributor.distribute(Collections.emptyList(), payout);
        assertThat(remainingChunk).isNotNull().isEqualTo(BigDecimal.ZERO);

        remainingChunk = this.distributor.distribute(Collections.singletonList(null), payout);
        assertThat(remainingChunk).isNotNull().isEqualTo(BigDecimal.ZERO);


        payout = BigDecimal.valueOf(123);
        remainingChunk = this.distributor.distribute(null, payout);
        assertThat(remainingChunk).isNotNull().isEqualTo(payout);

        remainingChunk = this.distributor.distribute(Collections.emptyList(), payout);
        assertThat(remainingChunk).isNotNull().isEqualTo(payout);

        remainingChunk = this.distributor.distribute(Collections.singletonList(null), payout);
        assertThat(remainingChunk).isNotNull().isEqualTo(payout);
    }

    @Test
    public void distributeSplitWithoutRules() {
        when(this.distributionRules.getSplitRules()).thenReturn(null);
        assertWithoutRules();

        when(this.distributionRules.getSplitRules()).thenReturn(Collections.emptyMap());
        assertWithoutRules();
    }

    private void assertWithoutRules() {
        ArgumentCaptor<List<PersonEquitySummaryVo>> captorPersonEquitySummaries = ArgumentCaptor.forClass(List.class);
        doReturn(new DistributionEquitySummaryVo())
                .when(equityScreener)
                .screenEquityDistributionSummary(captorPersonEquitySummaries.capture(), nullable(String.class));

        ArgumentCaptor<BigDecimal> captorDistributionAmount = ArgumentCaptor
                .forClass(BigDecimal.class);
        doNothing()
                .when(this.equityDistributor)
                .distributeEquity(any(DistributionEquitySummaryVo.class), captorDistributionAmount.capture());

        BigDecimal payout = BigDecimal.valueOf(8011.03);
        PersonEquitySummaryVo personEquitySummary = PersonEquitySummaryVo.builder()
                .personPayout(BigDecimal.ZERO)
                .personId(1)
                .equityType(null)
                .equityId(2)
                .equityQuantity(40)
                .build();
        when(this.distributionRules.getSplitRules())
                .thenReturn(null);

        BigDecimal remainingChunk = this.distributor.distribute(Collections.singletonList(personEquitySummary), payout);
        assertThat(remainingChunk)
                .isNotNull()
                .isEqualByComparingTo(BigDecimal.ZERO);

        BigDecimal capturedDistributionAmount = captorDistributionAmount.getValue();
        assertThat(capturedDistributionAmount).isNotNull().isEqualTo(payout);

        List<PersonEquitySummaryVo> capturedPersonEquitySummaries = captorPersonEquitySummaries.getValue();
        assertThat(capturedPersonEquitySummaries).isNotNull().hasSize(1);

        PersonEquitySummaryVo capturedPersonEquitySummary = capturedPersonEquitySummaries.get(0);
        assertThat(capturedPersonEquitySummary).isNotNull();
        assertThat(capturedPersonEquitySummary.getEquityType()).isNull();
        assertThat(capturedPersonEquitySummary.getPersonPayout()).isNotNull().isEqualTo(personEquitySummary.getPersonPayout());
        assertThat(capturedPersonEquitySummary.getPersonId()).isNotNull().isEqualTo(personEquitySummary.getPersonId());
        assertThat(capturedPersonEquitySummary.getEquityId()).isNotNull().isEqualTo(personEquitySummary.getEquityId());
        assertThat(capturedPersonEquitySummary.getEquityQuantity()).isNotNull().isEqualTo(personEquitySummary.getEquityQuantity());
    }

    @Test
    public void distributeSplitWithRulesNoChunk() {
        doReturn(new DistributionEquitySummaryVo())
                .when(equityScreener)
                .screenEquityDistributionSummary(any(), anyString());

        String classX = "CLASS_X";
        String classY = "CLASS_Y";
        BigDecimal payout = BigDecimal.valueOf(100);
        List<PersonEquitySummaryVo> personEquitySummaries = List.of(
                TestDataGenerator.personEquitySummary(classX, 40),
                TestDataGenerator.personEquitySummary(classY, 10)
        );

        Map<String, Function<BigDecimal, BigDecimal>> splitRules = Map.of(
                "CLASS_X", (distributionPayout) -> distributionPayout.multiply(BigDecimal.valueOf(0.6)),
                "CLASS_Y", (distributionPayout) -> distributionPayout.multiply(BigDecimal.valueOf(0.4))
        );
        when(this.distributionRules.getSplitRules())
                .thenReturn(splitRules);

        doNothing()
                .when(this.equityDistributor)
                .distributeEquity(any(DistributionEquitySummaryVo.class), eq(BigDecimal.valueOf(60.0)));

        doNothing()
                .when(this.equityDistributor)
                .distributeEquity(any(DistributionEquitySummaryVo.class), eq(BigDecimal.valueOf(40.0)));

        BigDecimal remainingChunk = this.distributor.distribute(personEquitySummaries, payout);
        assertThat(remainingChunk)
                .isNotNull()
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    public void distributeSplitWithRulesWithChunk() {
        doReturn(new DistributionEquitySummaryVo())
                .when(equityScreener)
                .screenEquityDistributionSummary(any(), anyString());

        String classX = "CLASS_X";
        String classY = "CLASS_Y";
        BigDecimal payout = BigDecimal.valueOf(200);
        List<PersonEquitySummaryVo> personEquitySummaries = List.of(
                TestDataGenerator.personEquitySummary(classX, 40),
                TestDataGenerator.personEquitySummary(classY, 10)
        );

        Map<String, Function<BigDecimal, BigDecimal>> splitRules = Map.of(
                "CLASS_X", (distributionPayout) -> distributionPayout.multiply(BigDecimal.valueOf(0.6)),
                "CLASS_Y", (distributionPayout) -> distributionPayout.multiply(BigDecimal.valueOf(0.4))
        );
        when(this.distributionRules.getSplitRules())
                .thenReturn(splitRules);

        when(this.distributionRules.getChunkAmount())
                .thenReturn(BigDecimal.valueOf(100));

        doNothing()
                .when(this.equityDistributor)
                .distributeEquity(any(DistributionEquitySummaryVo.class), eq(BigDecimal.valueOf(60.0)));

        doNothing()
                .when(this.equityDistributor)
                .distributeEquity(any(DistributionEquitySummaryVo.class), eq(BigDecimal.valueOf(40.0)));

        BigDecimal remainingChunk = this.distributor.distribute(personEquitySummaries, payout);
        assertThat(remainingChunk)
                .isNotNull()
                .isPositive();
    }

    @Test
    public void distributeSplitWithRulesWithPayoutLessThanChunk() {
        doReturn(new DistributionEquitySummaryVo())
                .when(equityScreener)
                .screenEquityDistributionSummary(any(), anyString());

        String classX = "CLASS_X";
        String classY = "CLASS_Y";
        BigDecimal payout = BigDecimal.valueOf(90);
        List<PersonEquitySummaryVo> personEquitySummaries = List.of(
                TestDataGenerator.personEquitySummary(classX, 40),
                TestDataGenerator.personEquitySummary(classY, 10)
        );

        Map<String, Function<BigDecimal, BigDecimal>> splitRules = Map.of(
                "CLASS_X", (distributionPayout) -> distributionPayout.multiply(BigDecimal.valueOf(0.6)),
                "CLASS_Y", (distributionPayout) -> distributionPayout.multiply(BigDecimal.valueOf(0.4))
        );
        when(this.distributionRules.getSplitRules())
                .thenReturn(splitRules);

        when(this.distributionRules.getChunkAmount())
                .thenReturn(BigDecimal.valueOf(100));

        doNothing()
                .when(this.equityDistributor)
                .distributeEquity(any(DistributionEquitySummaryVo.class), eq(BigDecimal.valueOf(54.0)));

        doNothing()
                .when(this.equityDistributor)
                .distributeEquity(any(DistributionEquitySummaryVo.class), eq(BigDecimal.valueOf(36.0)));

        BigDecimal remainingChunk = this.distributor.distribute(personEquitySummaries, payout);
        assertThat(remainingChunk)
                .isNotNull()
                .isZero();
    }
}
