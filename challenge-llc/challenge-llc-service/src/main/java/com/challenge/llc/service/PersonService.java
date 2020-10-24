package com.challenge.llc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.llc.domain.entity.Person;
import com.challenge.llc.domain.repo.interfaces.PersonRepository;

@RequiredArgsConstructor
@Slf4j
@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public List<Person> findWithCompanyEquity(UUID companyUuid) {
        Validate.notNull(companyUuid);

        return this.personRepository.findWithCompanyEquity(companyUuid);
    }
}
