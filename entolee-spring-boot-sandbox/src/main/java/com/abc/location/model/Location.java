package com.abc.location.model;

import com.abc.commands.subscription.api.CompanyCreatedEvent;
import com.github.entolee.annotations.DomainEventHandler;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "location")
@Getter
public class Location extends AbstractAggregateRoot<Location> {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "company_name", nullable = false)
    private String companyName;


    @DomainEventHandler(CompanyCreatedEvent.class)
    public static void onCompanyCreated(final CompanyCreatedEvent event,
                                        final EntityManager em) {
        final Location location = new Location();
        location.id = UUID.randomUUID();
        location.tenantId = event.getTenantId();
        location.companyId = event.getId();
        location.companyName = event.getName();
        em.persist(location);
    }
}
