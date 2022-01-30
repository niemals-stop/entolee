package com.github.entolee.impl;

interface SignalHandlerInvocationAdapter {

    void invoke(Object signal, final Object... args) throws Exception;
}
