package com.github.entolee.impl;

import java.lang.reflect.Method;

class InvocationTarget {
    private final Class<?> signalHandlerClass;
    private final Class<?> signalClass;
    private final Method method;

    InvocationTarget(Class<?> signalHandlerClass,
                     Class<?> signalClass,
                     Method method) {
        this.signalHandlerClass = signalHandlerClass;
        this.signalClass = signalClass;
        this.method = method;
    }

    public Class<?> getSignalHandlerClass() {
        return signalHandlerClass;
    }

    public Class<?> getSignalClass() {
        return signalClass;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "InvocationTarget{" +
            "signalHandlerClass=" + signalHandlerClass +
            ", signalClass=" + signalClass +
            '}';
    }
}
