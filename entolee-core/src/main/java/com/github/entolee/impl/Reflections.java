package com.github.entolee.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class Reflections {

    private Reflections() {
    }

    static void makeAccessible(Method method) {
        if (Modifier.isPublic(method.getModifiers()) || Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            return;
        }
        if (method.isAccessible()) {
            return;
        }
        method.setAccessible(true);
    }
}
