package com.challenge.llc.domain.repo.interfaces;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.challenge.llc.domain.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> findByNameIn(Set<String> name);

    @Query("SELECT p "
                   + " FROM Person p "
                   + "  JOIN p.equities e"
                   + "  JOIN e.company c "
                   + " WHERE c.uuid = :companyUuid"
                   + " GROUP BY p.id"
    )
    List<Person> findWithCompanyEquity(UUID companyUuid);
}
