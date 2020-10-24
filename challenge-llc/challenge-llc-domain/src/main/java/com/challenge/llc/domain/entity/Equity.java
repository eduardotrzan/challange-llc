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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import java.util.List;

import com.challenge.generic.domain.AbstractEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "equity")
@ToString(callSuper = true, of = { "id", "type", "quantity" })
public class Equity extends AbstractEntity<Long> {

    private static final String SEQUENCE_NAME = "equity_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "type", nullable = false, length = 70)
    private String type;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(targetEntity = Person.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(targetEntity = Company.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "equity", fetch = FetchType.LAZY)
    private List<EquityPayout> equityPayouts;
}
