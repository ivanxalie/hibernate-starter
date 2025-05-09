package org.example.mapper;

import org.example.dto.CompanyReadDto;
import org.example.entity.Company;
import org.hibernate.Hibernate;

public class CompanyReadMapper implements Mapper<Company, CompanyReadDto> {
    @Override
    public CompanyReadDto mapFrom(Company company) {
        if (company == null)
            return null;
        Hibernate.initialize(company.getLocales());
        return new CompanyReadDto(company.getId(), company.getName(), company.getLocales());
    }
}
