package com.abc.company;

import com.abc.commands.subscription.api.CreateCompanyCmd;
import com.abc.commands.subscription.api.DeleteCompanyCmd;
import com.abc.commands.subscription.api.UpdateCompanyCmd;
import com.abc.identifiy.model.UserAccount;
import com.github.entolee.core.DomainSignalPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Slf4j
public class EventProducer {

    private final DomainSignalPublisher publisher;

    @PostConstruct
    public void fire() {
        final UserAccount max = new UserAccount(UUID.randomUUID(), "max");
        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(() -> {
                try {
                    final UUID CompanyId = UUID.randomUUID();
                    final UUID tenantId = UUID.randomUUID();
                    final String CompanyName = RandomStringUtils.randomAlphabetic(8);

                    final CreateCompanyCmd cmd = new CreateCompanyCmd();
                    cmd.setId(CompanyId);
                    cmd.setTenantId(tenantId);
                    cmd.setName(CompanyName);
                    publisher.fire(cmd, max);

                    final UpdateCompanyCmd updateCmd = new UpdateCompanyCmd();
                    updateCmd.setId(CompanyId);
                    updateCmd.setTenantId(tenantId);
                    updateCmd.setName(CompanyName + " Updated");
                    publisher.fire(updateCmd, max);

                    final DeleteCompanyCmd deleteCmd = new DeleteCompanyCmd();
                    deleteCmd.setId(CompanyId);
                    deleteCmd.setTenantId(tenantId);
                    publisher.fire(deleteCmd, max);

                    log.info("\n");
                } catch (final Exception e) {
                    log.error(e.getMessage(), e);
                }
            }, 1000, 1000, TimeUnit.MILLISECONDS);
    }
}
