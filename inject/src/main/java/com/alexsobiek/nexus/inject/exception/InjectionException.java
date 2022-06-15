package com.alexsobiek.nexus.inject.exception;

public class InjectionException extends Exception {
    public InjectionException(String message) {
        super(message);
    }

    public InjectionException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public static void runtime(String message, Throwable throwable) throws RuntimeException {
        throw new RuntimeException(new InjectionException(message, throwable));
    }

    public static void runtime(String message) throws RuntimeException {
        throw new RuntimeException(new InjectionException(message));
    }
}
