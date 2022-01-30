package com.github.entolee.impl;

import java.lang.reflect.Modifier;
import java.util.Optional;

class EntityFactoryMethodInvocationAdapterBuilder implements SignalHandlerInvocationAdapterBuilder {

    private final SignalHandlerParamsResolver paramsResolver;

    EntityFactoryMethodInvocationAdapterBuilder(SignalHandlerParamsResolver paramsResolver) {
        this.paramsResolver = paramsResolver;
    }

    @Override
    public Optional<SignalHandlerInvocationAdapter> tryToBuild(InvocationTarget target) {
        if (Modifier.isStatic(target.getMethod().getModifiers())) {
            return Optional.of(new EntityFactoryMethodInvocationAdapter(paramsResolver, target));
        } else {
            return Optional.empty();
        }
    }
}
