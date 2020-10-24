package com.challenge.llc.domain.repo.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.llc.domain.entity.Equity;

@Repository
public interface EquityRepository extends JpaRepository<Equity, Long> {

}
