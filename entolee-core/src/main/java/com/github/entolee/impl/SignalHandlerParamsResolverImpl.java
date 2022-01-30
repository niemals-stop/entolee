package com.github.entolee.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;

class SignalHandlerParamsResolverImpl implements SignalHandlerParamsResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SignalHandlerParamsResolverImpl.class);

    private final Collection<SignalHandlerParamResolver> resolvers;

    SignalHandlerParamsResolverImpl(Collection<SignalHandlerParamResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public Object[] resolveParams(final Object signal, final Object[] signalArgs, final Method method) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            final Object arg = findInArgs(parameterType, signalArgs);
            if (signal.getClass().equals(parameterType)) {
                parameters[i] = signal;
            } else if (arg != null) {
                parameters[i] = arg;
            } else {
                parameters[i] = resolve(parameterType);
            }
        }
        return parameters;
    }

    private Object resolve(Class<?> parameterType) {
        for (SignalHandlerParamResolver resolver : resolvers) {
            final Optional<?> obj = resolver.resolve(parameterType);
            if (obj.isPresent()) {
                LOG.debug("Param type {} is resolved by {} with priority {}.",
                    parameterType, resolver.getClass().getSimpleName(), resolver.priority());
                return obj.get();
            }
        }
        LOG.debug("Unable to resolve type {} by {} resolvers.", parameterType, resolvers.size());
        return null;
    }

    private Object findInArgs(Class<?> parameterType, Object[] cmdArgs) {
        for (Object cmdArg : cmdArgs) {
            if (parameterType.isAssignableFrom(cmdArg.getClass())) {
                return cmdArg;
            }
        }
        return null;
    }
}
