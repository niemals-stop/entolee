package com.github.entolee.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;


class SignalHandlerInvocationBuilder<T extends Annotation> {

    private static final Logger LOG = LoggerFactory.getLogger(SignalHandlerInvocationBuilder.class);

    private SignalHandlerScanner scanner;
    private SignalHandlerInvocationAdapterFactory invocationFactory;
    private Function<T, Class<?>> valuextractor;
    private Class<T> annotation;

    public SignalHandlerInvocationBuilder<T> withScanner(SignalHandlerScanner scanner) {
        this.scanner = scanner;
        return this;
    }

    public SignalHandlerInvocationBuilder<T> withInvocationFactory(SignalHandlerInvocationAdapterFactory invocationFactory) {
        this.invocationFactory = invocationFactory;
        return this;
    }

    public SignalHandlerInvocationBuilder<T> withAnnotation(Class<T> annotation, Function<T, Class<?>> valuextractor) {
        this.annotation = annotation;
        this.valuextractor = valuextractor;
        return this;
    }

    public void build(BiConsumer<Class<?>, SignalHandlerInvocationAdapter> adapter) {
        final Instant startedAt = Instant.now();

        final Set<Class<?>> handlerClasses = scanner.scan();
        LOG.debug("Found {} handler(s).", handlerClasses.size());
        for (final Class<?> handlerClass : handlerClasses) {
            LOG.debug("Processing handler '{}'...", handlerClass);
            final Set<Method> methods = findMethods(handlerClass, annotation);
            LOG.debug("Handler '{}' has {} eligible method(s).", handlerClass, methods.size());
            for (Method handlerMethod : methods) {
                LOG.debug("Processing {}", handlerMethod);
                final Class<?> handingType = valuextractor.apply(handlerMethod.getAnnotation(annotation));
                final InvocationTarget target = new InvocationTarget(handlerClass, handingType, handlerMethod);
                adapter.accept(handingType, invocationFactory.create(target));
            }
            LOG.debug("Handlers for '{}' configured.", handlerClass);
        }
        LoggerFactory.getLogger(SignalHandlerRegistry.class)
            .info("Initialized in {} ms.", Duration.between(startedAt, Instant.now()).toMillis());
    }

    private Set<Method> findMethods(Class<?> handlerClass, Class<? extends Annotation> annotation) {
        final Set<Method> ret = new HashSet<>();
        for (Method method : handlerClass.getMethods()) {
            if (method.getAnnotation(annotation) != null) {
                ret.add(method);
            }
        }
        return ret;

    }
}
