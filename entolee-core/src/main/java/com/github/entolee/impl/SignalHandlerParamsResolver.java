package com.github.entolee.impl;

import java.lang.reflect.Method;

interface SignalHandlerParamsResolver {

    Object[] resolveParams(final Object signal, final Object[] signalArgs, Method method);
}
