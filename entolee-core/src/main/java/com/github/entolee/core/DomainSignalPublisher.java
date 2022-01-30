package com.github.entolee.core;

public interface DomainSignalPublisher {

    void fire(Object signal, final Object... args);
}
