package com.abc.company;

import com.abc.commands.subscription.api.DeactivateCompaniesCmd;
import com.abc.commands.subscription.api.DeactivateCompanyCmd;
import com.abc.identifiy.model.UserAccount;
import com.github.entolee.annotations.DomainCmdHandler;
import com.github.entolee.core.DomainSignalPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class CompanyDeactivationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyDeactivationService.class);

    private final DomainSignalPublisher publisher;

    CompanyDeactivationService(final DomainSignalPublisher publisher) {
        this.publisher = publisher;
    }

    @DomainCmdHandler(DeactivateCompaniesCmd.class)
    void deactivateCompanies(final DeactivateCompaniesCmd cmd, final UserAccount account) {
        cmd.getIds().forEach(id -> {
            publisher.fire(new DeactivateCompanyCmd(id, cmd.getTenantId()), account);
            LOG.info("Company {} deactivated by {}.", id, account.getId());
        });
    }
}
