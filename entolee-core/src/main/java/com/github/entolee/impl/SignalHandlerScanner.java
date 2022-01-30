package com.github.entolee.impl;

import java.util.Set;

interface SignalHandlerScanner {

    /**
     * Get all classes which may contain handler for a command or event.
     *
     * @return a set of classes.
     */
    Set<Class<?>> scan();
}
