package com.github.entolee.impl;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class BeanSignalHandlerInvocationAdapter implements SignalHandlerInvocationAdapter {

    private final SignalHandlerParamsResolver paramsResolver;
    private final Object bean;
    private final Method method;

    BeanSignalHandlerInvocationAdapter(final Object bean,
                                       final Method method,
                                       final SignalHandlerParamsResolver paramsResolver) {
        this.bean = bean;
        this.method = prepare(bean, method);
        this.paramsResolver = paramsResolver;
        ReflectionUtils.makeAccessible(this.method);
    }

    private Method prepare(Object bean, Method beanMethod) {
        final Class<?> beanType = bean.getClass();
        final Method methodToUse = BridgeMethodResolver.findBridgedMethod(AopUtils.selectInvocableMethod(beanMethod, beanType));
        final Method targetMethod = (!Proxy.isProxyClass(beanType) ? AopUtils.getMostSpecificMethod(methodToUse, beanType) : methodToUse);
        Reflections.makeAccessible(targetMethod);
        return targetMethod;
    }

    @Override
    public void invoke(Object signal, Object... args) throws Exception {
        method.invoke(bean, paramsResolver.resolveParams(signal, args, method));
    }
}
