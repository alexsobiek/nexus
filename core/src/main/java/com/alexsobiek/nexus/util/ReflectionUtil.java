package com.alexsobiek.nexus.util;

import java.lang.reflect.ParameterizedType;

public class ReflectionUtil {
    public static Class<?> getGenericParameter(Class<?> _class, int index) {
        return (Class<?>) ((ParameterizedType) _class.getGenericSuperclass()).getActualTypeArguments()[index];
    }
}
