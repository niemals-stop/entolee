package com.abc.department.model;

import com.abc.commands.subscription.api.CompanyUpdatedEvent;
import com.abc.commands.subscription.api.CreateDepartmentCmd;
import com.abc.identifiy.model.UserAccount;
import com.github.entolee.annotations.DomainCmdHandler;
import com.github.entolee.annotations.DomainEventHandler;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "department")
@Getter
@NamedQueries({
    @NamedQuery(
        name = "Target.Department.CompanyUpdatedEvent",
        query = "select d from Department as d where d.companyId=:id and d.tenantId =:tenantId"),
})
public class Department {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @DomainCmdHandler(CreateDepartmentCmd.class)
    public static void create(final CreateDepartmentCmd cmd, final EntityManager entityManager) {
        final Department department = new Department();
        department.id = cmd.getId();
        department.tenantId = cmd.getTenantId();
        department.companyId = cmd.getCompanyId();
        department.companyName = cmd.getCompanyName();
        department.name = cmd.getName();
        entityManager.persist(department);
    }

    @DomainEventHandler(CompanyUpdatedEvent.class)
    public void onCompanyUpdated(final CompanyUpdatedEvent event,
                                 final EntityManager entityManager,
                                 final UserAccount account) {
        this.companyName = event.getName();
        entityManager.merge(this);
    }
}
