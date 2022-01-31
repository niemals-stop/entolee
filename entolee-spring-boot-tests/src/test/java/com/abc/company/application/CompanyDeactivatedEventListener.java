package com.abc.company.application;

import com.abc.commands.subscription.api.CompanyDeactivatedEvent;
import com.github.entolee.annotations.DomainEventHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class CompanyDeactivatedEventListener {

    private final List<CompanyDeactivatedEvent> events = new CopyOnWriteArrayList<>();

    @DomainEventHandler(CompanyDeactivatedEvent.class)
    void recalculateCompaniesInLocation(CompanyDeactivatedEvent event) {
        events.add(event);
    }

    @DomainEventHandler(CompanyDeactivatedEvent.class)
    void recalculateCompaniesInLocationAgain(CompanyDeactivatedEvent event) {
        events.add(event);
    }

    public List<CompanyDeactivatedEvent> getEvents() {
        return events;
    }

    public void clear() {
        events.clear();
    }

}
