package com.github.entolee.impl;

import com.github.entolee.core.DomainSignalPublisher;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
        final List<SignalHandlerInvocationAdapter> adapters = handlers.find(signal.getClass());
        if (adapters.isEmpty()) {
            invoke(missingSignalHandlerStrategy.get(signal.getClass()), signal, args);
        } else {
            for (SignalHandlerInvocationAdapter adapter : adapters) {
                invoke(adapter, signal, args);
            }
        }
    }

    private void invoke(SignalHandlerInvocationAdapter adapter, Object signal, Object[] args) {
        try {
            adapter.invoke(signal, args);
        } catch (InvocationTargetException e) {
            throwAsUncheckedException(e.getTargetException());
        } catch (Exception e) {
            throwAsUncheckedException(e);
        }
    }
}
