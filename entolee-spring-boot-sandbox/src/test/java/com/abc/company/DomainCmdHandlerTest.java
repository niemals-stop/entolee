package com.abc.company;

import com.abc.IntegrationsTest;
import com.abc.commands.subscription.api.CompanyCreatedEvent;
import com.abc.commands.subscription.api.CompanyDeletedEvent;
import com.abc.commands.subscription.api.CompanyUpdatedEvent;
import com.abc.commands.subscription.api.CreateCompanyCmd;
import com.abc.commands.subscription.api.DeleteCompanyCmd;
import com.abc.commands.subscription.api.TypeMismatchedCmd;
import com.abc.commands.subscription.api.UpdateCompanyCmd;
import com.abc.company.application.CompanyRepository;
import com.abc.company.model.Company;
import com.abc.company.model.CompanyValidationException;
import com.abc.identifiy.model.UserAccount;
import com.github.entolee.core.DomainSignalPublisher;
import com.github.entolee.impl.TypeMismatchException;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@IntegrationsTest
class DomainCmdHandlerTest {

    private final UserAccount currentUser = new UserAccount(UUID.randomUUID(), "max");
    private final UUID tenantId = UUID.fromString("47f13d4c-c608-4502-9619-d62ee9e63c9b");
    @Autowired
    private DomainSignalPublisher publisher;
    @Autowired
    private CompanyRepository repository;
    @Autowired
    private RecordingAuditEventListener auditEventListener;

    @BeforeEach
    void setUp() {
        auditEventListener.clear();
    }

    @Test
    void handlerForFactoryMethodWorks() {
        final UUID companyId = UUID.randomUUID();
        final CreateCompanyCmd cmd = createCreateCompanyCmd(companyId);

        publisher.fire(cmd, currentUser);

        Assertions.assertThat(repository.findById(companyId))
            .isNotEmpty();
    }

    @Test
    void handlerForUpdateEntityMethodWorks() {
        final UUID companyId = UUID.randomUUID();
        publisher.fire(createCreateCompanyCmd(companyId), currentUser);
        Assertions.assertThat(repository.findAll())
            .extracting(Company::getId)
            .contains(companyId);

        final String newName = "New Name";
        publisher.fire(new UpdateCompanyCmd(companyId, tenantId, newName), currentUser);

        Assertions.assertThat(repository.findById(companyId))
            .map(Company::getName)
            .hasValue(newName);

        Assertions.assertThat(auditEventListener.getRecordedEvents())
            .extracting(e -> e.getClass().getSimpleName())
            .containsExactly(
                CompanyCreatedEvent.class.getSimpleName(),
                CompanyUpdatedEvent.class.getSimpleName()
            );
    }

    @Test
    void runtimeExceptionTypeAndItsMsgPropogatedProperly() {
        final UUID companyId = UUID.randomUUID();
        final CreateCompanyCmd createCompanyCmd = createCreateCompanyCmd(companyId);
        publisher.fire(createCompanyCmd, currentUser);
        Assertions.assertThat(repository.findAll())
            .extracting(Company::getId)
            .contains(companyId);

        final String newName = "Ooops New Name";
        Assertions.assertThatThrownBy(() -> publisher.fire(new UpdateCompanyCmd(companyId, tenantId, newName), currentUser))
            .hasMessage("Wrong company name.")
            .isInstanceOf(CompanyValidationException.class);

        Assertions.assertThat(repository.findById(companyId))
            .as("entity hasn't been updated.")
            .map(Company::getName)
            .hasValue(createCompanyCmd.getName());

        Assertions.assertThat(auditEventListener.getRecordedEvents())
            .as("only create event record. update didn't succeed.")
            .extracting(e -> e.getClass().getSimpleName())
            .containsExactly(CompanyCreatedEvent.class.getSimpleName());
    }

    @Test
    void handlerForDeleteEntityMethodWorks() {
        final UUID company1Id = UUID.randomUUID();
        final UUID company2Id = UUID.randomUUID();
        publisher.fire(createCreateCompanyCmd(company1Id), currentUser);
        publisher.fire(createCreateCompanyCmd(company2Id), currentUser);
        Assertions.assertThat(repository.findAll())
            .extracting(Company::getId)
            .contains(company1Id, company2Id);

        final DeleteCompanyCmd deleteCompanyCmd = new DeleteCompanyCmd()
            .withId(company1Id)
            .withTenantId(tenantId);

        publisher.fire(deleteCompanyCmd, currentUser);

        Assertions.assertThat(repository.findAll())
            .extracting(Company::getId)
            .contains(company2Id);

        Assertions.assertThat(auditEventListener.getRecordedEvents())
            .extracting(e -> e.getClass().getSimpleName())
            .containsExactly(
                CompanyCreatedEvent.class.getSimpleName(),
                CompanyCreatedEvent.class.getSimpleName(),
                CompanyDeletedEvent.class.getSimpleName()
            );
    }

    @Test
    void typeMismatchThrownWhenParamTypesDifferent() {
        final UUID companyId = UUID.randomUUID();
        final CreateCompanyCmd cmd = createCreateCompanyCmd(companyId);
        publisher.fire(cmd, currentUser);

        Assertions.assertThatThrownBy(() -> publisher.fire(new TypeMismatchedCmd(1, false)))
            .isInstanceOf(TypeMismatchException.class)
            .hasMessage("com.abc.commands.subscription.api.TypeMismatchedCmd#tenantId = Boolean, com.abc.company.model.Company#tenantId = UUID");
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