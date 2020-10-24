package com.challenge.llc.service.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.llc.domain.entity.Company;
import com.challenge.llc.dto.response.CompanyDto;

@Component
public class CompanyMapper {

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<CompanyDto> toDto(Company entity) {
        if (entity == null) {
            return Optional.empty();
        }

        CompanyDto dto = CompanyDto.builder()
                .uuid(entity.getUuid())
                .createDate(entity.getCreateDate())
                .updateDate(entity.getUpdateDate())
                .name(entity.getName())
                .build();
        return Optional.of(dto);
    }
}
