package com.github.entolee.impl;

import org.springframework.stereotype.Component;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Set;
import java.util.TreeSet;

@Component
class EntityNamedQueryFinder {

    String findNamedQuery(InvocationTarget target) {
        final Class<?> signalClass = target.getSignalClass();
        final Class<?> entityClass = target.getSignalHandlerClass();
        final String any = "Target." + entityClass.getSimpleName() + ".Any";
        final String certain = "Target." + entityClass.getSimpleName() + "." + signalClass.getSimpleName();
        final Set<String> allNamedQueries = findAllNamedQueries(entityClass);
        if (allNamedQueries.contains(certain)) {
            return certain;
        } else if (allNamedQueries.contains(any)) {
            return any;
        } else {
            throw new IllegalArgumentException(
                String.format("A named query '%s' or '%s' must be defined on the entity '%s'.", certain, any, entityClass)
            );
        }

    }

    private Set<String> findAllNamedQueries(Class<?> entityClass) {
        final Set<String> namedQueries = new TreeSet<>();
        final NamedQueries namedQueriesAnnotation = entityClass.getAnnotation(NamedQueries.class);
        if (namedQueriesAnnotation != null) {
            for (NamedQuery namedQuery : namedQueriesAnnotation.value()) {
                namedQueries.add(namedQuery.name());
            }
        }

        final NamedQuery namedQueryAnnotation = entityClass.getAnnotation(NamedQuery.class);
        if (namedQueryAnnotation != null) {
            namedQueries.add(namedQueryAnnotation.name());
        }
        return namedQueries;
    }
}
