package com.abc.company;

import com.abc.IntegrationsTest;
import com.abc.commands.subscription.api.CausingNPEEvent;
import com.abc.commands.subscription.api.CompanyDeletedEvent;
import com.abc.commands.subscription.api.CreateCompanyCmd;
import com.abc.commands.subscription.api.CreateDepartmentCmd;
import com.abc.commands.subscription.api.UpdateCompanyCmd;
import com.abc.department.model.Department;
import com.abc.identifiy.model.UserAccount;
import com.abc.location.model.Location;
import com.github.entolee.core.DomainSignalPublisher;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationsTest
public class DomainEventHandlerTest {

    private final UserAccount currentUser = new UserAccount(UUID.randomUUID(), "max");
    private final UUID tenantId = UUID.fromString("47f13d4c-c608-4502-9619-d62ee9e63c9b");
    @Autowired
    private DomainSignalPublisher publisher;
    @Autowired
    private TestEntityManager em;

    @Test
    void handlerEventsWhenNoListeners() {
        final UUID companyId = UUID.randomUUID();
        final String companyName = "Fabrika M";
        Assertions.assertThatCode(() -> {
            publisher.fire(new CreateCompanyCmd(companyId, tenantId, companyName), currentUser);

            final String companyNameNew = "Mega Fabrika M";
            publisher.fire(new UpdateCompanyCmd(companyId, tenantId, companyNameNew), currentUser);
        }).doesNotThrowAnyException();

    }

    @Test
    void handlerEventsWithMultipleDestinations() {
        final UUID companyId = UUID.randomUUID();
        final String companyName = "Fabrika M";
        publisher.fire(new CreateCompanyCmd(companyId, tenantId, companyName), currentUser);

        final UUID department1Id = UUID.randomUUID();
        publisher.fire(new CreateDepartmentCmd(department1Id, tenantId, "IT", companyId, companyName), currentUser);

        final UUID department2Id = UUID.randomUUID();
        publisher.fire(new CreateDepartmentCmd(department2Id, tenantId, "Marketing", companyId, companyName), currentUser);

        final String companyNameNew = "Mega Fabrika M";
        publisher.fire(new UpdateCompanyCmd(companyId, tenantId, companyNameNew), currentUser);

        assertThat(em.find(Department.class, department1Id).getCompanyName())
            .isEqualTo(companyNameNew);

        assertThat(em.find(Department.class, department2Id).getCompanyName())
            .isEqualTo(companyNameNew);
    }

    @Test
    void noExceptionThrowWhenNoDomainEventListener() {
        Assertions.assertThatCode(() -> publisher.fire(new CompanyDeletedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())))
            .as("event handlers are optional.")
            .doesNotThrowAnyException();
    }

    @Test
    void handlerEventsOnFactoryMethods() {
        final UUID companyId = UUID.randomUUID();
        final String companyName = "Fabrika M";

        Assertions.assertThatCode(() -> publisher.fire(new CreateCompanyCmd(companyId, tenantId, companyName), currentUser))
            .doesNotThrowAnyException();

        final List<Location> locations = em.getEntityManager()
            .createQuery("select l from Location l where l.companyId =:companyId", Location.class)
            .setParameter("companyId", companyId)
            .getResultList();

        assertThat(locations)
            .hasSize(1)
            .extracting(Location::getCompanyName)
            .containsExactly(companyName);
    }

    @Test
    void runtimeExceptionSignalHandlingPropagated() {
        Assertions.assertThatThrownBy(() -> publisher.fire(new CausingNPEEvent(null, null)))
            .isInstanceOf(NullPointerException.class);
    }
}
