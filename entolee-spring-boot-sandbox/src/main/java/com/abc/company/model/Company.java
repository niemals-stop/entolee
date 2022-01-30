package com.abc.company.model;

import com.abc.commands.subscription.api.CausingNPEEvent;
import com.abc.commands.subscription.api.CompanyCreatedEvent;
import com.abc.commands.subscription.api.CompanyDeletedEvent;
import com.abc.commands.subscription.api.CompanyUpdatedEvent;
import com.abc.commands.subscription.api.CreateCompanyCmd;
import com.abc.commands.subscription.api.DeleteCompanyCmd;
import com.abc.commands.subscription.api.TypeMismatchedCmd;
import com.abc.commands.subscription.api.UpdateCompanyCmd;
import com.abc.company.TenantResource;
import com.abc.company.application.CompanyRepository;
import com.abc.identifiy.model.UserAccount;
import com.github.entolee.annotations.DomainCmdHandler;
import com.github.entolee.core.DomainSignalPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "Target.Company.UpdateCompanyCmd",
        query = "select c from Company as c where c.id=:id and c.tenantId =:tenantId"),
    @NamedQuery(
        name = "Target.Company.Any",
        query = "select c from Company as c where c.id=:id and c.tenantId =:tenantId")
})
@Table(name = "company")
public class Company extends AbstractAggregateRoot<Company> implements TenantResource {

    private static final Logger LOG = LoggerFactory.getLogger(Company.class);

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @DomainCmdHandler(CreateCompanyCmd.class)
    public static void create(final CreateCompanyCmd cmd,
                              final CompanyRepository repository,
                              final DomainSignalPublisher publisher,
                              final UserAccount account) {
        final Company company = new Company();
        company.id = cmd.getId();
        company.tenantId = cmd.getTenantId();
        company.name = cmd.getName();
        final CompanyCreatedEvent event = new CompanyCreatedEvent(
            company.id,
            company.tenantId,
            company.name,
            account.getId()
        );
        company.registerEvent(event);

        repository.save(company);
        LOG.info("Company.created");
        publisher.fire(event, account);
    }

    @DomainCmdHandler(CausingNPEEvent.class)
    public static void whenNPEDuringSignalHandling(final CausingNPEEvent event) {
        event.getTenantId().compareTo(Boolean.TRUE);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public UUID getTenantId() {
        return tenantId;
    }

    @Override
    public String getName() {
        return name;
    }

    @DomainCmdHandler(UpdateCompanyCmd.class)
    public void update(final UpdateCompanyCmd cmd,
                       final CompanyRepository repository,
                       final CompanyValidator validator,
                       final DomainSignalPublisher publisher,
                       final UserAccount account) {
        validator.validate(cmd);
        this.name = cmd.getName();
        final CompanyUpdatedEvent event = new CompanyUpdatedEvent(id, tenantId, name, account.getId());
        publisher.fire(event, account);
        registerEvent(event);
        repository.save(this);
        LOG.info("Company.updated");
    }

    @DomainCmdHandler(DeleteCompanyCmd.class)
    public void delete(final DeleteCompanyCmd cmd,
                       final CompanyRepository repository,
                       final DomainSignalPublisher publisher,
                       final UserAccount account) {
        LOG.info("Company.delete");
        final CompanyDeletedEvent signal = new CompanyDeletedEvent(id, tenantId, account.getId());
        publisher.fire(signal, account);
        registerEvent(signal);
        repository.delete(this);
        LOG.info("Company.deleted");
    }

    @DomainCmdHandler(TypeMismatchedCmd.class)
    public void paramTypeMismatchTestCase(final TypeMismatchedCmd cmd) {
        throw new IllegalArgumentException("should never happen");
    }

}
