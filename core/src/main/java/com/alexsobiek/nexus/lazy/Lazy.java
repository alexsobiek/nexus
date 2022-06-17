package com.alexsobiek.nexus.lazy;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Lazy<T> implements LazyLike<T> {
    private final Optional<Supplier<T>> lazySupplier;
    private T val;

    /**
     * Creates a new Lazy
     * @param lazySupplier Supplier for this Lazy's object type
     */
    public Lazy(Supplier<T> lazySupplier) {
        this.lazySupplier = Optional.of(lazySupplier);
    }

    protected Lazy() {
        lazySupplier = Optional.empty();
    }

    /**
     * Creates a new empty Lazy
     * @return Lazy
     * @param <T>
     */
    public static <T> Lazy<T> delayed() {
        return new Lazy<>();
    }

    /**
     * Gets the value of this lazy. If not present, calls supplier
     * @return T value
     */
    @Override
    public T get() {
        synchronized (this) {
            if (val == null) {
                if (lazySupplier.isPresent()) val = lazySupplier.get().get();
                else throw new RuntimeException("Attempted to get lazy that is empty!");
            }
            return val;
        }
    }

    /**
     * Calls consumer if this lazy has been supplied
     * @param consumer Consumer to call if present
     */
    public void ifPresent(Consumer<T> consumer) {
        if (exists()) consumer.accept(get());
    }

    /**
     * Calls consumer if this lazy has been supplied. If not, calls runnable.
     * @param consumer Consumer to call if present
     * @param runnable Runnable to run if not present
     */
    public void ifPresentOrElse(Consumer<T> consumer, Runnable runnable) {
        if (exists()) consumer.accept(get());
        else runnable.run();
    }

    /**
     * Returns true if this Lazy has a value
     * @return Boolean
     */
    public boolean exists() {
        return val != null;
    }

    /**
     * Sets this Lazy's value. Will override an existing value.
     * @param value Value to set or replace the current value
     */
    public void set(T value) {
        this.val = value;
    }

    /**
     * Sets this Lazy's value if it is empty.
     * @param value Value to set
     */
    public void setIfAbsent(T value) {
        if (val == null) this.val = value;
    }
}
