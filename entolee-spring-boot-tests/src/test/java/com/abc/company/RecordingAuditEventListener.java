package com.abc.company;

import com.abc.commands.subscription.api.CompanyCreatedEvent;
import com.abc.commands.subscription.api.CompanyDeletedEvent;
import com.abc.commands.subscription.api.CompanyUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RecordingAuditEventListener {

    private final List<Object> events = new CopyOnWriteArrayList<>();

    @EventListener({
        CompanyCreatedEvent.class,
        CompanyUpdatedEvent.class,
        CompanyDeletedEvent.class
    })
    public void onUpdate(Object event) {
        events.add(event);
    }

    public void clear() {
        events.clear();
    }

    public List<Object> getRecordedEvents() {
        return events;
    }
}
