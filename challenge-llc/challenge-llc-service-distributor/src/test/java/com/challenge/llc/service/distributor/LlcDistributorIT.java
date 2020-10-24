package com.challenge.llc.service.distributor;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.challenge.llc.service.distributor.vo.DistributionRulesVo;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;
import com.challenge.llc.service.testutils.TestDataGenerator;

@Slf4j
public class LlcDistributorIT {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.DOWN;
    private static final int SCALE = 2;

    private List<IDistributor> distributors;

    @BeforeEach
    public void setup() {
        IEquityScreener equityScreener = new EquityScreener();
        IEquityDistributor equityDistributor = new ProportionEquityDistributor(SCALE, ROUNDING_MODE);
        this.distributors = List.of(
                this.firstChunkDistributor(equityScreener, equityDistributor),
                this.secondChunkDistributor(equityScreener, equityDistributor),
                this.restChunkDistributor(equityScreener, equityDistributor)
        );
    }

    public IDistributor firstChunkDistributor(IEquityScreener equityScreener, IEquityDistributor equityDistributor) {
        Map<String, Function<BigDecimal, BigDecimal>> splitRules = Map.of(
                "CLASS_X", (payout) -> payout
        );
        DistributionRulesVo distributionRules = DistributionRulesVo.builder()
                .chunkAmount(BigDecimal.valueOf(100))
                .splitRules(splitRules)
                .build();
        return new Distributor(SCALE, ROUNDING_MODE, distributionRules, equityScreener, equityDistributor);
    }

    public IDistributor secondChunkDistributor(IEquityScreener equityScreener, IEquityDistributor equityDistributor) {
        Map<String, Function<BigDecimal, BigDecimal>> splitRules = Map.of(
                "CLASS_X", (payout) -> payout.multiply(BigDecimal.valueOf(0.5)),
                "CLASS_Y", (payout) -> payout.multiply(BigDecimal.valueOf(0.5))
        );
        DistributionRulesVo distributionRules = DistributionRulesVo.builder()
                .chunkAmount(BigDecimal.valueOf(100))
                .splitRules(splitRules)
                .build();
        return new Distributor(SCALE, ROUNDING_MODE, distributionRules, equityScreener, equityDistributor);
    }

    public IDistributor restChunkDistributor(IEquityScreener equityScreener, IEquityDistributor equityDistributor) {
        return new Distributor(SCALE, ROUNDING_MODE, new DistributionRulesVo(), equityScreener, equityDistributor);
    }

    @Test
    public void computePayout100() {
        runPayoutAssertion(100, 80, 20, 0);
    }

    @Test
    public void computePayout200() {
        runPayoutAssertion(200, 120, 55, 25);
    }

    @Test
    public void computePayout300() {
        runPayoutAssertion(300, 170, 86.25, 43.75);
    }

    @Test
    public void computePayout400() {
        runPayoutAssertion(400, 220, 117.5, 62.5);
    }

    @Test
    public void computePayout3001() {
        runPayoutAssertion(3001, 1520.50, 930.30, 550.18);
    }

    @Test
    public void computePayout8011() {
        runPayoutAssertion(8011, 4025.50, 2495.93, 1489.56);
    }

    @Test
    public void computePayout8011_03() {
        runPayoutAssertion(8011.03, 4025.51, 2495.93, 1489.56);
    }

    @Test
    public void computePayout100_7() {
        runPayoutAssertion(100.07, 80.02, 20.01, 0.01);
    }

    @Test
    public void testRange() {
        double payout = 0d;
        BigDecimal payoutDecimal = BigDecimal.valueOf(0);

        while (payout <= 3000.01) {
            // Operations with double could generate decimals with rounding issues.
            payout += 0.01;
            BigDecimal payoutValue = new BigDecimal(payout);
            assertLeftoverLessOrEqualsWithInitialTestData(payoutValue);

            payoutDecimal = payoutDecimal.add(BigDecimal.valueOf(0.01));
            assertLeftoverLessOrEqualsWithInitialTestData(payoutDecimal);
        }
    }

    private Pair<BigDecimal, List<PersonEquitySummaryVo>> assertLeftoverLessOrEqualsWithInitialTestData(BigDecimal payout) {
        List<PersonEquitySummaryVo> personEquitySummaries = TestDataGenerator.initialPersonEquitySummaries();
        BigDecimal leftOver = this.calculate(payout, personEquitySummaries);

        BigDecimal totalPaid = personEquitySummaries
                .stream()
                .map(PersonEquitySummaryVo::getPersonPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(totalPaid)
                .withFailMessage(String.format("totalPaid %s should be less or equals to %s", totalPaid, payout))
                .isLessThanOrEqualTo(payout);

        return Pair.of(leftOver, personEquitySummaries);
    }

    private void runPayoutAssertion(double payout, double person1Payout, double person2Payout, double person3Payout) {
        var pair = assertLeftoverLessOrEqualsWithInitialTestData(BigDecimal.valueOf(payout));
        List<PersonEquitySummaryVo> personEquitySummaries = pair.getRight();

        BigDecimal leftOver = pair.getKey();
        this.assertCannotDivide(leftOver, 3);

        assertAndDisplay(personEquitySummaries, person1Payout, person2Payout, person3Payout);
    }

    private void assertCannotDivide(BigDecimal leftOver, int numberOfPeople) {
        if (leftOver.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal remainder = leftOver.remainder(new BigDecimal(numberOfPeople));
        log.info("Processed if leftOver={} can be divided by numberOfPeople={} as remainder={}",
                leftOver, numberOfPeople, remainder);
        assertThat(remainder)
                .withFailMessage(String.format("Left Over %s can still be divided by %s.", leftOver, numberOfPeople))
                .isNotEqualByComparingTo(BigDecimal.ZERO);
    }

    private BigDecimal calculate(BigDecimal payout, List<PersonEquitySummaryVo> personEquitySummaries) {
        BigDecimal newPayout = payout;
        for (IDistributor distributor : this.distributors) {
            newPayout = distributor.distribute(personEquitySummaries, newPayout);
        }
        return newPayout;
    }

    private void assertAndDisplay(
            List<PersonEquitySummaryVo> personEquitySummaries,
            double person1Payout,
            double person2Payout,
            double person3Payout) {
        var personDistribution = new HashMap<UUID, BigDecimal>();

        for (PersonEquitySummaryVo personEquitySummary : personEquitySummaries) {
            UUID personUuid = personEquitySummary.getPersonUuid();
            BigDecimal currentPayout = personDistribution.getOrDefault(personUuid, BigDecimal.ZERO);
            BigDecimal newPayout = currentPayout
                    .add(personEquitySummary.getPersonPayout())
                    .setScale(SCALE, ROUNDING_MODE);
            personDistribution.put(personUuid, newPayout);
        }

        List<Pair<UUID, BigDecimal>> pairs = personEquitySummaries
                .stream()
                .map(p -> Pair.of(p.getPersonUuid(), p.getPersonPayout()))
                .collect(Collectors.toList());

        log.info("final personEquitySummaries={} and expectedDistribution={}", pairs, personDistribution);
        assertValues(personDistribution, personEquitySummaries.get(0).getPersonUuid(), person1Payout);
        assertValues(personDistribution, personEquitySummaries.get(1).getPersonUuid(), person2Payout);
        assertValues(personDistribution, personEquitySummaries.get(2).getPersonUuid(), person3Payout);
    }

    private void assertValues(Map<UUID, BigDecimal> personDistribution, UUID index, double personPayout) {
        BigDecimal computedPerson = personDistribution.get(index);
        assertThat(computedPerson)
                .withFailMessage(String.format("The computed value=%s for person index=%s is not equals to expectedValue=%s",
                        computedPerson, index, personPayout))
                .isEqualByComparingTo(BigDecimal.valueOf(personPayout));
    }

}
