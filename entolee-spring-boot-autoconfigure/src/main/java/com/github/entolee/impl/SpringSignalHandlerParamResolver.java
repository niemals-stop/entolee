package com.github.entolee.impl;

import org.springframework.context.ApplicationContext;

import java.util.Optional;

class SpringSignalHandlerParamResolver implements SignalHandlerParamResolver {

    private static final int PRIORITY = 1000;
    private final ApplicationContext ctx;

    public SpringSignalHandlerParamResolver(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public <T> Optional<T> resolve(Class<T> paramType) {
        return Optional.of(ctx.getBean(paramType));
    }

    @Override
    public int priority() {
        return PRIORITY;
    }
}
