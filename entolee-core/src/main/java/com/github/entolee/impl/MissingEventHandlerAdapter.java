package com.github.entolee.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum MissingEventHandlerAdapter implements SignalHandlerInvocationAdapter {

    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(MissingEventHandlerAdapter.class);

    @Override
    public void invoke(Object signal, Object... args) {
        LOG.info("No handler for event {} was registered.", signal.getClass());
    }
}
