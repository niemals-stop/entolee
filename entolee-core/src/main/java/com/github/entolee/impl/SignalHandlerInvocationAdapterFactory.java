package com.github.entolee.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


class SignalHandlerInvocationAdapterFactory {

    private final List<SignalHandlerInvocationAdapterBuilder> builders;

    SignalHandlerInvocationAdapterFactory(final List<SignalHandlerInvocationAdapterBuilder> builders) {
        this.builders = builders;
    }

    SignalHandlerInvocationAdapter create(final InvocationTarget target) {
        final List<SignalHandlerInvocationAdapter> adapters = builders.stream()
            .map(b -> b.tryToBuild(target))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        if (adapters.size() != 1) {
            throw new IllegalArgumentException("More than one invocation adapter found for " + target);
        }
        return adapters.get(0);
    }
}
