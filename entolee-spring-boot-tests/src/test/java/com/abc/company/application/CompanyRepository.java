package com.abc.company.application;

import com.abc.company.model.Company;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends CrudRepository<Company, UUID> {

    @Query("select c from Company c where c.tenantId=:tenantId and c.status = 'ACTIVE'")
    List<Company> findActiveCompanies(@Param("tenantId") final UUID tenantId);
}
