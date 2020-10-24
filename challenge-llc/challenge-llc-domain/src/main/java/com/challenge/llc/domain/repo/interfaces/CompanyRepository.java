package com.challenge.llc.domain.repo.interfaces;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.llc.domain.entity.Company;
import com.challenge.llc.domain.entity.Equity;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUuid(UUID companyUuid);
    Optional<Company> findByName(String name);
}
