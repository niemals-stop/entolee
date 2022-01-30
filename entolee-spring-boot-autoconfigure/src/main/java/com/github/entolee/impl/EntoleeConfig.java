package com.github.entolee.impl;

import com.github.entolee.annotations.DomainCmdHandler;
import com.github.entolee.annotations.DomainEventHandler;
import com.github.entolee.core.DomainSignalPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class EntoleeConfig {

    @Bean
    public SignalHandlerRegistry handlerRegistry(final SignalHandlerScanner scanner,
                                                 final SignalHandlerInvocationAdapterFactory factory) {
        final SignalHandlerRegistry registry = new SignalHandlerRegistry();
        new SignalHandlerInvocationBuilder<DomainCmdHandler>()
            .withScanner(scanner)
            .withInvocationFactory(factory)
            .withAnnotation(DomainCmdHandler.class, DomainCmdHandler::value)
            .build(registry::register);
        new SignalHandlerInvocationBuilder<DomainEventHandler>()
            .withScanner(scanner)
            .withInvocationFactory(factory)
            .withAnnotation(DomainEventHandler.class, DomainEventHandler::value)
            .build(registry::register);
        return registry;
    }

    @Bean
    public EntityLoaderFactory entityLoader(final EntityManager entityManager,
                                            final EntityNamedQueryFinder entityNamedQueryFinder) {
        return new JpaEntityLoader(entityManager, entityNamedQueryFinder);
    }

    @Bean
    SignalHandlerParamResolver springSignalHandlerParamResolver(final ApplicationContext ctx) {
        return new SpringSignalHandlerParamResolver(ctx);
    }

    @Bean
    public SignalHandlerParamsResolver domainCommandHandlerParamsResolver(final Collection<SignalHandlerParamResolver> resolvers) {
        final List<SignalHandlerParamResolver> orderedResolvers = resolvers.stream()
            .sorted(Comparator.comparing(SignalHandlerParamResolver::priority))
            .collect(Collectors.toList());
        return new SignalHandlerParamsResolverImpl(Collections.unmodifiableCollection(orderedResolvers));
    }

    @Bean
    public MissingSignalHandlerStrategy missingSignalHandlerStrategy() {
        return new MissingSignalHandlerStrategyImpl();
    }

    @Bean
    public DomainSignalPublisher domainCommandPublisher(final SignalHandlerRegistry registry,
                                                        final MissingSignalHandlerStrategy missingSignalHandlerStrategy) {
        return new DomainSignalPublisherImpl(registry, missingSignalHandlerStrategy);
    }

    @Bean
    public SignalHandlerScanner domainCmdHandlerScanner(final ApplicationContext ctx) {
        return new JpaEntityScannerSignal(ctx);
    }

    @Bean
    public SignalHandlerInvocationAdapterFactory domainCmdHandlerInvocationAdapterFactory(final SignalHandlerParamsResolver paramsResolver,
                                                                                          final EntityLoaderFactory entityLoaderFactory) {
        return new SignalHandlerInvocationAdapterFactory(Arrays.asList(
            new EntityFactoryMethodInvocationAdapterBuilder(paramsResolver),
            new EntityMethodInvocationAdapterBuilder(paramsResolver, entityLoaderFactory))
        );
    }

}
