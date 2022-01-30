package com.github.entolee.impl;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;

import javax.persistence.Entity;
import java.util.Set;

@AllArgsConstructor
class JpaEntityScannerSignal implements SignalHandlerScanner {

    private final ApplicationContext ctx;

    @Override
    public Set<Class<?>> scan() {
        final EntityScanner entityScanner = new EntityScanner(ctx);
        try {
            return entityScanner.scan(Entity.class);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
