package com.challenge.llc.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;

import com.challenge.generic.domain.AbstractEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "split_rule")
@ToString(callSuper = true, of = { "id" })
public class SplitRule extends AbstractEntity<Long> {

    private static final String SEQUENCE_NAME = "split_rule_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "equity_type", nullable = false, length = 70)
    private String equityType;

    @Column(name = "percentage_allocated", nullable = false)
    private BigDecimal percentageAllocated;

    @ManyToOne(targetEntity = EquitySplitRule.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equity_split_rule_id", nullable = false)
    private EquitySplitRule equitySplitRule;
}
