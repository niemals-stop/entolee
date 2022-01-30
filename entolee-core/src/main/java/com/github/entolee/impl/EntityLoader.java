package com.github.entolee.impl;

import java.util.List;

/**
 * An interface to load entities to process the given signal. The signal or carries all needed information for the predicate.
 */
interface EntityLoader {

    List<Object> load(final Class<?> entityClass, final Object cmd);

}
