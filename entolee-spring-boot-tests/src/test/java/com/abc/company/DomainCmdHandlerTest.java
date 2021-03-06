package com.abc.company;

import com.abc.IntegrationsTest;
import com.abc.commands.subscription.api.CompanyDeactivatedEvent;
import com.abc.commands.subscription.api.CreateCompanyCmd;
import com.abc.commands.subscription.api.DeactivateCompaniesCmd;
import com.abc.commands.subscription.api.DeleteCompanyCmd;
import com.abc.commands.subscription.api.TypeMismatchedCmd;
import com.abc.commands.subscription.api.UpdateCompanyCmd;
import com.abc.company.application.CompanyDeactivatedEventListener;
import com.abc.company.application.CompanyRepository;
import com.abc.company.model.Company;
import com.abc.company.model.CompanyValidationException;
import com.abc.identifiy.model.UserAccount;
import com.github.entolee.core.DomainSignalPublisher;
import com.github.entolee.impl.TypeMismatchException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationsTest
class DomainCmdHandlerTest {

    private final UserAccount currentUser = new UserAccount(UUID.randomUUID(), "max");
    private final UUID tenantId = UUID.fromString("47f13d4c-c608-4502-9619-d62ee9e63c9b");

    @Autowired
    private DomainSignalPublisher publisher;

    @Autowired
    private CompanyRepository repository;

    @Autowired
    private CompanyDeactivatedEventListener companyDeactivatedEventListener;

    @Test
    void handlerForFactoryMethodWorks() {
        final UUID companyId = UUID.randomUUID();
        final CreateCompanyCmd cmd = createCreateCompanyCmd(companyId);

        publisher.fire(cmd, currentUser);

        assertThat(repository.findById(companyId))
            .isNotEmpty();
    }

    @Test
    void handlerForUpdateEntityMethodWorks() {
        final UUID companyId = UUID.randomUUID();
        publisher.fire(createCreateCompanyCmd(companyId), currentUser);
        assertThat(repository.findAll())
            .extracting(Company::getId)
            .contains(companyId);

        final String newName = "New Name";
        publisher.fire(new UpdateCompanyCmd(companyId, tenantId, newName), currentUser);

        assertThat(repository.findById(companyId))
            .map(Company::getName)
            .hasValue(newName);
    }

    @Test
    void runtimeExceptionTypeAndItsMsgPropogatedProperly() {
        final UUID companyId = UUID.randomUUID();
        final CreateCompanyCmd createCompanyCmd = createCreateCompanyCmd(companyId);
        publisher.fire(createCompanyCmd, currentUser);
        assertThat(repository.findAll())
            .extracting(Company::getId)
            .contains(companyId);

        final String newName = "Ooops New Name";
        assertThatThrownBy(() -> publisher.fire(new UpdateCompanyCmd(companyId, tenantId, newName), currentUser))
            .hasMessage("Wrong company name.")
            .isInstanceOf(CompanyValidationException.class);

        assertThat(repository.findById(companyId))
            .as("entity hasn't been updated.")
            .map(Company::getName)
            .hasValue(createCompanyCmd.getName());
    }

    @Test
    void handlerForDeleteEntityMethodWorks() {
        final UUID company1Id = UUID.randomUUID();
        final UUID company2Id = UUID.randomUUID();
        publisher.fire(createCreateCompanyCmd(company1Id), currentUser);
        publisher.fire(createCreateCompanyCmd(company2Id), currentUser);
        assertThat(repository.findAll())
            .extracting(Company::getId)
            .contains(company1Id, company2Id);

        final DeleteCompanyCmd deleteCompanyCmd = new DeleteCompanyCmd()
            .withId(company1Id)
            .withTenantId(tenantId);

        publisher.fire(deleteCompanyCmd, currentUser);

        assertThat(repository.findAll())
            .extracting(Company::getId)
            .contains(company2Id);
    }

    @Test
    void typeMismatchThrownWhenParamTypesDifferent() {
        final UUID companyId = UUID.randomUUID();
        final CreateCompanyCmd cmd = createCreateCompanyCmd(companyId);
        publisher.fire(cmd, currentUser);

        assertThatThrownBy(() -> publisher.fire(new TypeMismatchedCmd(1, false)))
            .isInstanceOf(TypeMismatchException.class)
            .hasMessage("com.abc.commands.subscription.api.TypeMismatchedCmd#tenantId = Boolean, com.abc.company.model.Company#tenantId = UUID");
    }

    @Test
    void springBeanIsAbleToHandleCommand() {
        final UUID company1Id = UUID.randomUUID();
        publisher.fire(createCreateCompanyCmd(company1Id), currentUser);

        final UUID company2Id = UUID.randomUUID();
        publisher.fire(createCreateCompanyCmd(company2Id), currentUser);

        final List<UUID> companies = Arrays.asList(company1Id, company2Id);
        assertThat(repository.findAllById(companies))
            .extracting(Company::getId)
            .containsExactlyInAnyOrder(company1Id, company2Id);

        publisher.fire(new DeactivateCompaniesCmd(companies, tenantId), currentUser);
        assertThat(repository.findActiveCompanies(tenantId))
            .isEmpty();
        assertThat(repository.findAllById(companies))
            .extracting(Company::getId)
            .containsExactlyInAnyOrder(company1Id, company2Id);

        assertThat(companyDeactivatedEventListener.getEvents())
            .extracting(CompanyDeactivatedEvent::getId)
            .as("company deactivated event listener executed four times, because it has two event listeners.")
            .containsExactly(
                company1Id, company1Id,
                company2Id, company2Id
            );
    }

    private CreateCompanyCmd createCreateCompanyCmd(UUID companyId) {
        final String companyName = RandomStringUtils.randomAlphabetic(8);
        final CreateCompanyCmd cmd = new CreateCompanyCmd();
        cmd.setId(companyId);
        cmd.setTenantId(tenantId);
        cmd.setName(companyName);
        return cmd;
    }
}