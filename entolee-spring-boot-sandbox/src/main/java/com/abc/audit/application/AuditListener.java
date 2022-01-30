package com.abc.audit.application;

import com.abc.commands.subscription.api.CompanyCreatedEvent;
import com.abc.commands.subscription.api.CompanyDeletedEvent;
import com.abc.commands.subscription.api.CompanyUpdatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class AuditListener {

    private final ObjectMapper json;

    @EventListener({
        CompanyCreatedEvent.class,
        CompanyUpdatedEvent.class,
        CompanyDeletedEvent.class
    })
    public void onUpdate(Object event) throws JsonProcessingException {
        log.info("{}", json.writeValueAsString(event));
    }

}
