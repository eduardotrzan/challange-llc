package com.challenge.llc.domain.testutils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.llc.domain.entity.Company;
import com.challenge.llc.domain.entity.Equity;
import com.challenge.llc.domain.entity.EquityPayout;
import com.challenge.llc.domain.entity.EquitySplitRule;
import com.challenge.llc.domain.entity.Person;
import com.challenge.llc.domain.entity.SplitRule;
import com.challenge.llc.domain.repo.interfaces.CompanyRepository;
import com.challenge.llc.domain.repo.interfaces.EquityPayoutRepository;
import com.challenge.llc.domain.repo.interfaces.EquityRepository;
import com.challenge.llc.domain.repo.interfaces.EquitySplitRuleRepository;
import com.challenge.llc.domain.repo.interfaces.PersonRepository;
import com.challenge.llc.domain.repo.interfaces.SplitRuleRepository;
import com.devskiller.jfairy.Fairy;

@Slf4j
@RequiredArgsConstructor
@Component
public class DatabasePrefillUtils {

    private final CompanyRepository companyRepository;

    private final EquityPayoutRepository equityPayoutRepository;

    private final EquityRepository equityRepository;

    private final EquitySplitRuleRepository equitySplitRuleRepository;

    private final SplitRuleRepository splitRuleRepository;

    private final PersonRepository personRepository;


    private Company company;

    private Equity equity;

    private EquityPayout equityPayout;

    private EquitySplitRule equitySplitRule;

    private SplitRule splitRule;

    private Person person;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DatabasePrefillContext {

        private final Company company;

        private final Equity equity;

        private final EquityPayout equityPayout;

        private final EquitySplitRule equitySplitRule;

        private final SplitRule splitRule;

        private final Person person;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DatabasePrefillContext saveAndGet() {
        doSave();
        DatabasePrefillContext databasePrefillContext = DatabasePrefillContext.builder()
                .company(this.company)
                .equitySplitRule(this.equitySplitRule)
                .equity(this.equity)
                .equityPayout(this.equityPayout)
                .splitRule(this.splitRule)
                .person(this.person)
                .build();
        flush();
        return databasePrefillContext;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save() {
        doSave();
        flush();
    }

    private void doSave() {
        this.saveCompany();
        this.saveEquitySplitRule();
        this.saveSplitRule();
        this.savePerson();
        this.saveEquity();
        this.saveEquityPayout();
    }

    private void flush() {
        this.company = null;
        this.equitySplitRule = null;
        this.splitRule = null;
        this.person = null;
        this.equity = null;
        this.equityPayout = null;
    }

    private void saveCompany() {
        log.info("Saving company...");
        if (this.company == null) {
            log.info("company is null, skipping save.");
            return;
        }
        this.company = this.companyRepository.save(this.company);
        log.info("Saved company={}.", this.company);
    }

    private void saveEquitySplitRule() {
        log.info("Saving equitySplitRule...");
        if (this.equitySplitRule == null) {
            log.info("equitySplitRule is null, skipping save.");
            return;
        }

        if (this.company == null) {
            log.info("company is null, skipping save.");
            return;
        }

        this.equitySplitRule.setCompany(this.company);
        this.equitySplitRule = this.equitySplitRuleRepository.save(this.equitySplitRule);
        log.info("Saved equitySplitRule={}.", this.equitySplitRule);
    }

    private void saveSplitRule() {
        log.info("Saving splitRule...");
        if (this.splitRule == null) {
            log.info("splitRule is null, skipping save.");
            return;
        }

        if (this.equitySplitRule == null) {
            log.info("equitySplitRule is null, skipping save.");
            return;
        }

        this.splitRule.setEquitySplitRule(this.equitySplitRule);
        this.splitRule = this.splitRuleRepository.save(this.splitRule);
        log.info("Saved splitRule={}.", this.splitRule);
    }

    private void savePerson() {
        log.info("Saving person...");
        if (this.person == null) {
            log.info("person is null, skipping save.");
            return;
        }
        this.person = this.personRepository.save(this.person);
        log.info("Saved person={}.", this.person);
    }

    private void saveEquity() {
        log.info("Saving equity...");

        if (this.equity == null) {
            log.info("equity is null, skipping save.");
            return;
        }

        if (this.company == null) {
            log.info("company is null, skipping save.");
            return;
        }

        if (this.person == null) {
            log.info("person is null, skipping save.");
            return;
        }

        this.equity.setCompany(this.company);
        this.equity.setPerson(this.person);
        this.equity = this.equityRepository.save(this.equity);
        log.info("Saved equity={}.", this.equity);
    }

    private void saveEquityPayout() {
        log.info("Saving equityPayout...");

        if (this.equityPayout == null) {
            log.info("equityPayout is null, skipping save.");
            return;
        }

        if (this.equity == null) {
            log.info("equity is null, skipping save.");
            return;
        }

        this.equityPayout.setEquity(this.equity);
        this.equityPayout = this.equityPayoutRepository.save(this.equityPayout);
        log.info("Saved equityPayout={}.", this.equityPayout);
    }

    public DatabasePrefillUtils withRandomPerson() {
        com.devskiller.jfairy.producer.person.Person person = Fairy.create()
                .person();
        this.person = Person.builder()
                .name(person.getFullName())
                .build();
        return this;
    }

    public DatabasePrefillUtils withRandomEquity() {
        this.equity = Equity.builder()
                .type(String.format("type-%s", this.randomIntegerBetween(1, 5)))
                .quantity(this.randomIntegerBetween(100, 500))
                .build();
        return this;
    }

    private Integer randomIntegerBetween(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    private <T extends Enum<T>> T randomEnum(Class<T> clazz) {
        var list = new ArrayList<>(EnumSet.allOf(clazz));
        int enumAt = randomIntegerBetween(0, list.size() - 1);
        return list.get(enumAt);
    }
}
