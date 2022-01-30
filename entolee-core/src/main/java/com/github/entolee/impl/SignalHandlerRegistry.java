package com.github.entolee.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


class SignalHandlerRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(SignalHandlerRegistry.class);

    private final Map<Class<?>, SignalHandlerInvocationAdapter> payloadTypeToAdapter;

    SignalHandlerRegistry() {
        this.payloadTypeToAdapter = new ConcurrentHashMap<>();
    }

    public Optional<SignalHandlerInvocationAdapter> find(final Class<?> signalClazz) {
        return Optional.ofNullable(payloadTypeToAdapter.get(signalClazz));
    }

    public void register(final Class<?> signalClazz, final SignalHandlerInvocationAdapter adapter) {
        synchronized (payloadTypeToAdapter) {
            final SignalHandlerInvocationAdapter existingAdapter = payloadTypeToAdapter.get(signalClazz);
            if (existingAdapter == null) {
                payloadTypeToAdapter.put(signalClazz, adapter);
                LOG.debug("Signal '{}' registered and will be handled by {}.", signalClazz, adapter);
            } else {
                throw new IllegalArgumentException(
                    String.format("For signal %s a handler %s has been registered previously.", signalClazz, adapter)
                );
            }
        }
    }
}
