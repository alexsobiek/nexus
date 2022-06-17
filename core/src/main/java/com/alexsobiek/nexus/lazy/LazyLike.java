package com.alexsobiek.nexus.lazy;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface LazyLike<T> extends Supplier<T> {
    void ifPresent(Consumer<T> consumer);
    void ifPresentOrElse(Consumer<T> consumer, Runnable runnable);
    boolean exists();
}
