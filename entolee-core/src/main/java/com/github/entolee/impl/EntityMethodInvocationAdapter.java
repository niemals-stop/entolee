package com.github.entolee.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

class EntityMethodInvocationAdapter implements SignalHandlerInvocationAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(EntityMethodInvocationAdapter.class);

    private final EntityLoader entityLoader;
    private final SignalHandlerParamsResolver paramsResolver;
    private final Class<?> entityClass;
    private final Method method;

    EntityMethodInvocationAdapter(final EntityLoader entityLoader,
                                  final SignalHandlerParamsResolver paramsResolver,
                                  final InvocationTarget target) {

        this.entityLoader = entityLoader;
        this.paramsResolver = paramsResolver;
        this.entityClass = target.getSignalHandlerClass();
        this.method = target.getMethod();
    }

    @Override
    public void invoke(Object signal, final Object... args) throws Exception {
        final List<Object> entities = entityLoader.load(entityClass, signal);
        LOG.debug("Loaded {} entities of type {} for signal {}.", entities.size(), entityClass, signal.getClass().getSimpleName());

        final Object[] params = paramsResolver.resolveParams(signal, args, method);
        for (Object entity : entities) {
            method.invoke(entity, params);
        }
    }

    @Override
    public String toString() {
        return "'" + getClass() + "' for '" + method + "'";
    }
}
