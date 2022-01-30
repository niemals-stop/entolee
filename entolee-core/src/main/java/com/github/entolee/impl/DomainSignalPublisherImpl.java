package com.github.entolee.impl;

import com.github.entolee.core.DomainSignalPublisher;

import java.lang.reflect.InvocationTargetException;

class DomainSignalPublisherImpl implements DomainSignalPublisher {

    private final SignalHandlerRegistry handlers;
    private final MissingSignalHandlerStrategy missingSignalHandlerStrategy;

    DomainSignalPublisherImpl(final SignalHandlerRegistry handlers,
                              final MissingSignalHandlerStrategy missingSignalHandlerStrategy) {
        this.handlers = handlers;
        this.missingSignalHandlerStrategy = missingSignalHandlerStrategy;
    }

    private static RuntimeException throwAsUncheckedException(Throwable t) {
        throwAs(t);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwAs(Throwable t) throws T {
        throw (T) t;
    }

    @Override
    public void fire(Object signal, final Object... args) {
        final SignalHandlerInvocationAdapter adapter = handlers.find(signal.getClass())
            .orElseGet(() -> missingSignalHandlerStrategy.get(signal.getClass()));
        try {
            adapter.invoke(signal, args);
        } catch (InvocationTargetException e) {
            throwAsUncheckedException(e.getTargetException());
        } catch (Exception e) {
            throwAsUncheckedException(e);
        }
    }
}
