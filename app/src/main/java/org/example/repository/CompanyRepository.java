package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.Company;

public class CompanyRepository extends BaseRepository<Integer, Company> {
    public CompanyRepository(EntityManager manager) {
        super(manager, Company.class);
    }
}
