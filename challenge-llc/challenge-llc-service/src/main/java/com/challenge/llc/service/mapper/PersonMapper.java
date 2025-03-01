package com.challenge.llc.service.mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.llc.domain.entity.Equity;
import com.challenge.llc.domain.entity.Person;
import com.challenge.llc.dto.response.PersonDto;
import com.challenge.llc.service.distributor.vo.PersonEquitySummaryVo;

@Component
public class PersonMapper {

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<PersonDto> toDto(Person entity) {
        if (entity == null) {
            return Optional.empty();
        }

        PersonDto dto = PersonDto.builder()
                .uuid(entity.getUuid())
                .createDate(entity.getCreateDate())
                .updateDate(entity.getUpdateDate())
                .name(entity.getName())
                .build();
        return Optional.of(dto);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<PersonDto> toDtos(List<Person> entities) {
        return Objects.requireNonNullElse(entities, Collections.<Person>emptyList())
                .stream()
                .map(this::toDto)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<PersonEquitySummaryVo> toVos(List<Person> entities) {
        return Objects.requireNonNullElse(entities, Collections.<Person>emptyList())
                .stream()
                .map(this::toVos)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<PersonEquitySummaryVo> toVos(Person entity) {
        if (entity == null || CollectionUtils.isEmpty(entity.getEquities())) {
            return Collections.emptyList();
        }

        return entity.getEquities()
                .stream()
                .map(e -> this.toVo(entity, e))
                .collect(Collectors.toList());
    }

    private PersonEquitySummaryVo toVo(Person person, Equity equity) {
        return PersonEquitySummaryVo.builder()
                .personId(person.getId())
                .personPayout(BigDecimal.ZERO)
                .equityId(equity.getId())
                .equityType(equity.getType())
                .equityQuantity(equity.getQuantity())
                .build();
    }
}
