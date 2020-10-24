package com.challenge.llc.domain.repo.interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.challenge.llc.domain.entity.EquitySplitRule;

@Repository
public interface EquitySplitRuleRepository extends JpaRepository<EquitySplitRule, Long> {

    @Query("SELECT e "
                   + " FROM EquitySplitRule e"
                   + " JOIN e.company c "
                   + "  WHERE c.uuid = :companyUuid")
    List<EquitySplitRule> findByCompanyUuid(UUID companyUuid);

}
