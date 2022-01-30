package com.github.entolee.impl;

import java.lang.reflect.Method;

class EntityFactoryMethodInvocationAdapter implements SignalHandlerInvocationAdapter {

    private final SignalHandlerParamsResolver paramsResolver;
    private final Class<?> entityClass;
    private final Method factoryMethod;

    EntityFactoryMethodInvocationAdapter(final SignalHandlerParamsResolver paramsResolver,
                                         final InvocationTarget target) {
        this.paramsResolver = paramsResolver;
        this.entityClass = target.getSignalHandlerClass();
        this.factoryMethod = target.getMethod();

    }

    @Override
    public void invoke(Object signal, final Object... args) throws Exception {
        final Object[] params = paramsResolver.resolveParams(signal, args, factoryMethod);
        factoryMethod.invoke(entityClass, params);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " for " + factoryMethod;
    }
}
