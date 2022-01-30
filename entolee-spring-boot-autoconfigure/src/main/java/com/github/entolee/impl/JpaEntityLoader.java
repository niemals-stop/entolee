package com.github.entolee.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
class JpaEntityLoader implements EntityLoaderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(JpaEntityLoader.class);

    private final EntityManager entityManager;
    private final EntityNamedQueryFinder entityNamedQueryFinder;

    @Override
    public EntityLoader create(InvocationTarget target) {
        final String nameQuery = entityNamedQueryFinder.findNamedQuery(target);
        return new ByNamedQueryLoader(entityManager, nameQuery);
    }

    private static class ByNamedQueryLoader implements EntityLoader {

        private final EntityManager entityManager;
        private final String nameQuery;

        private ByNamedQueryLoader(EntityManager entityManager, String nameQuery) {
            this.entityManager = entityManager;
            this.nameQuery = nameQuery;
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<Object> load(Class<?> entityClass, Object signal) {
            final Query query = entityManager.createNamedQuery(nameQuery);
            LOG.debug("Loading entity {} by named query {} to process signal {}.",
                entityClass.getSimpleName(), nameQuery, signal.getClass().getSimpleName());

            final BeanWrapper cmdBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(signal);
            replicateCmdParamsToQuery(cmdBeanWrapper, query, entityClass);
            return (List<Object>) query.getResultList();
        }

        private void replicateCmdParamsToQuery(final BeanWrapper cmdBeanWrapper, final Query query, Class<?> entityClass) {
            for (Parameter<?> paramQuery : query.getParameters()) {
                final String paramName = paramQuery.getName();
                final Class<?> paramTypeQuery = paramQuery.getParameterType();
                final Class<?> paramTypeCmd = cmdBeanWrapper.getPropertyType(paramName);
                if (!Objects.equals(paramTypeCmd, paramTypeQuery)) {
                    throw new TypeMismatchException(String.format("%s#%s = %s, %s#%s = %s",
                        cmdBeanWrapper.getWrappedClass().getName(),
                        paramName,
                        paramTypeCmd == null ? null : paramTypeCmd.getSimpleName(),
                        entityClass.getName(),
                        paramName,
                        paramTypeQuery.getSimpleName()));
                }
                final Object paramValue = cmdBeanWrapper.getPropertyValue(paramName);
                query.setParameter(paramName, paramValue);
            }
        }
    }
}
