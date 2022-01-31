package com.github.entolee.impl;

import com.github.entolee.annotations.DomainCmdHandler;
import com.github.entolee.annotations.DomainEventHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

@Component
class SignalHandlerBeanPostProcessor implements BeanPostProcessor {

    private final SignalHandlerRegistry registry;
    private final SignalHandlerParamsResolver paramsResolver;

    SignalHandlerBeanPostProcessor(final SignalHandlerRegistry registry,
                                   final SignalHandlerParamsResolver paramsResolver) {
        this.registry = registry;
        this.paramsResolver = paramsResolver;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        process(bean, DomainCmdHandler.class, DomainCmdHandler::value);
        process(bean, DomainEventHandler.class, DomainEventHandler::value);
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private <T extends Annotation> void process(final Object bean,
                                                final Class<T> annotation,
                                                final Function<T, Class<?>> annotationValueExtractor) {
        final Map<Method, T> handlers = MethodIntrospector.selectMethods(
            bean.getClass(),
            (MethodIntrospector.MetadataLookup<T>) method -> AnnotatedElementUtils.findMergedAnnotation(method, annotation));

        for (Map.Entry<Method, T> e : handlers.entrySet()) {
            final Method method = e.getKey();
            final Class<?> signalType = annotationValueExtractor.apply(e.getValue());
            final BeanSignalHandlerInvocationAdapter invocationAdapter = new BeanSignalHandlerInvocationAdapter(bean, method, paramsResolver);
            registry.register(signalType, invocationAdapter);
        }
    }
}
