package com.github.entolee.impl;

interface MissingSignalHandlerStrategy {

    SignalHandlerInvocationAdapter get(final Class<?> signalClass);
}
