package com.challenge.llc.domain.repo.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.llc.domain.entity.SplitRule;

@Repository
public interface SplitRuleRepository extends JpaRepository<SplitRule, Long> {

}
