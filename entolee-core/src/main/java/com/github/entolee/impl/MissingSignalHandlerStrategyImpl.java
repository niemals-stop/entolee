package com.github.entolee.impl;

class MissingSignalHandlerStrategyImpl implements MissingSignalHandlerStrategy {

    @Override
    public SignalHandlerInvocationAdapter get(Class<?> signalClass) {
        if (signalClass.getSimpleName().endsWith("Event")) {
            return MissingEventHandlerAdapter.INSTANCE;
        } else {
            throw new IllegalStateException("There has been no handler for " + signalClass.getSimpleName() + " registered.");
        }
    }

}
