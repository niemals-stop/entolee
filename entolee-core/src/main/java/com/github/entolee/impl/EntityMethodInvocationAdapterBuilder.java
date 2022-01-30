package com.github.entolee.impl;

import java.lang.reflect.Modifier;
import java.util.Optional;

class EntityMethodInvocationAdapterBuilder implements SignalHandlerInvocationAdapterBuilder {

    private final SignalHandlerParamsResolver paramsResolver;
    private final EntityLoaderFactory entityLoaderFactory;

    EntityMethodInvocationAdapterBuilder(final SignalHandlerParamsResolver paramsResolver,
                                         final EntityLoaderFactory entityLoaderFactory) {
        this.paramsResolver = paramsResolver;
        this.entityLoaderFactory = entityLoaderFactory;
    }

    @Override
    public Optional<SignalHandlerInvocationAdapter> tryToBuild(InvocationTarget target) {
        if (Modifier.isStatic(target.getMethod().getModifiers())) {
            return Optional.empty();
        } else {
            return Optional.of(new EntityMethodInvocationAdapter(entityLoaderFactory.create(target), paramsResolver, target));
        }
    }
}
