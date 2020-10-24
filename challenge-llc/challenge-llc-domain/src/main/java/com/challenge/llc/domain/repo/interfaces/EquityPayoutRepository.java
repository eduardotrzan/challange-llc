package com.challenge.llc.domain.repo.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.llc.domain.entity.EquityPayout;

@Repository
public interface EquityPayoutRepository extends JpaRepository<EquityPayout, Long> {

}
