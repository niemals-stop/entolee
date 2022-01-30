package com.github.entolee.impl;

import java.util.Optional;

public interface SignalHandlerParamResolver {

    int HIGHEST_PRIORITY = 0;
    int LOWEST_PRIORITY = Integer.MAX_VALUE;

    <T> Optional<T> resolve(Class<T> paramType);

    int priority();
}
