package com.github.entolee.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


class SignalHandlerRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(SignalHandlerRegistry.class);

    private final Map<Class<?>, List<SignalHandlerInvocationAdapter>> payloadTypeToAdapter;

    SignalHandlerRegistry() {
        this.payloadTypeToAdapter = new ConcurrentHashMap<>();
    }

    public List<SignalHandlerInvocationAdapter> find(final Class<?> signalClazz) {
        final List<SignalHandlerInvocationAdapter> adapters = payloadTypeToAdapter.get(signalClazz);
        if (adapters == null) {
            return Collections.emptyList();
        }
        return adapters;
    }

    public void register(final Class<?> signalClazz, final SignalHandlerInvocationAdapter adapter) {
        synchronized (payloadTypeToAdapter) {
            final List<SignalHandlerInvocationAdapter> existingAdapters = payloadTypeToAdapter.get(signalClazz);
            if (existingAdapters == null) {
                payloadTypeToAdapter.put(signalClazz, Collections.singletonList(adapter));
                LOG.debug("Handler {} for signal '{}' registered.", adapter, signalClazz);
            } else {
                final List<SignalHandlerInvocationAdapter> adapters = new ArrayList<>(existingAdapters);
                adapters.add(adapter);
                payloadTypeToAdapter.put(signalClazz, Collections.unmodifiableList(adapters));
                LOG.debug("Handler {} for signal '{}' registered. Total amount of handlers {}.", adapter, signalClazz, adapters.size());
            }
        }
    }
}
