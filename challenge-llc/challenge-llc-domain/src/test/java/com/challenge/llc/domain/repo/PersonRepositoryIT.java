package com.challenge.llc.domain.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.challenge.generic.domain.AbstractEntity;
import com.challenge.llc.domain.entity.Equity;
import com.challenge.llc.domain.entity.Person;
import com.challenge.llc.domain.repo.interfaces.CompanyRepository;
import com.challenge.llc.domain.repo.interfaces.PersonRepository;
import com.challenge.llc.domain.testutils.AbstractDomainRepoIT;

@Slf4j
public class PersonRepositoryIT extends AbstractDomainRepoIT {

    private static final String DEFAULT_COMPANY_NAME = "Krakatoa Ventures";

    private static final String JESSICA = "Jessica";
    private static final String HENRY = "Henry";
    private static final String LISA = "Lisa";

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void saveOrder() {
        Person person = super.getDatabasePrefillUtils()
                .withRandomPerson()
                .saveAndGet()
                .getPerson();
        assertThat(person).isNotNull();
        assertThat(person.getId()).isNotNull();
    }

    @Test
    public void findAfterSave() {
        Long personId = super.getDatabasePrefillUtils()
                .withRandomPerson()
                .saveAndGet()
                .getPerson()
                .getId();
        Optional<Person> person = this.personRepository.findById(personId);
        assertThat(person).isNotNull().isPresent();
        assertThat(person.get().getId())
                .isNotNull()
                .isEqualTo(personId);
    }

    @Test
    public void findDefaultNames() {
        List<Person> persons = this.personRepository.findByNameIn(Set.of(
                JESSICA,
                HENRY,
                LISA
        ));
        assertThat(persons).isNotNull().hasSize(3);
        persons.forEach(this::assertDefaultPersons);
    }

    private void assertDefaultPersons(Person person) {
        switch (person.getName()) {
            case JESSICA:
                assertThat(person.getEquities()).isNotNull().hasSize(1);
                Equity equityJessica = person.getEquities().get(0);
                assertThat(equityJessica.getQuantity()).isNotNull().isEqualTo(40);
                assertThat(equityJessica.getType()).isNotNull().isEqualTo("CLASS_X");
                break;
            case HENRY:
                assertThat(person.getEquities()).isNotNull().hasSize(2);
                person.getEquities().sort(Comparator.comparingInt(Equity::getQuantity));

                Equity equityHenry = person.getEquities().get(0);
                assertThat(equityHenry.getQuantity()).isEqualTo(10);
                assertThat(equityHenry.getType()).isEqualTo("CLASS_X");

                equityHenry = person.getEquities().get(1);
                assertThat(equityHenry.getQuantity()).isEqualTo(15);
                assertThat(equityHenry.getType()).isEqualTo("CLASS_Y");
                break;
            case LISA:
                assertThat(person.getEquities()).isNotNull().hasSize(1);
                Equity equityLisa = person.getEquities().get(0);
                assertThat(equityLisa.getQuantity()).isNotNull().isEqualTo(15);
                assertThat(equityLisa.getType()).isNotNull().isEqualTo("CLASS_Y");
                break;
            default:
                fail("Not a valid initial name");
        }
    }

    @Test
    public void findPersonsAtCompany() {
        UUID companyUuid = this.companyRepository
                .findByName(DEFAULT_COMPANY_NAME)
                .map(AbstractEntity::getUuid)
                .orElseThrow();

        List<Person> persons = this.personRepository.findWithCompanyEquity(companyUuid);
        assertThat(persons).isNotNull().hasSize(3);
        persons.forEach(this::assertDefaultPersons);
    }
}
