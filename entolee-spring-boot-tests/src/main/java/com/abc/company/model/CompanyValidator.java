package com.abc.company.model;

import com.abc.commands.subscription.api.UpdateCompanyCmd;
import org.springframework.stereotype.Component;


@Component
class CompanyValidator {

    void validate(final UpdateCompanyCmd cmd) {
        if (cmd.getName().startsWith("Ooops")) {
            throw new CompanyValidationException("Wrong company name.");
        }
    }
}
