package com.alexsobiek.nexus.inject.dependency;

import com.alexsobiek.nexus.inject.annotation.Inject;
import com.alexsobiek.nexus.util.CollectionUtil;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class DependencyProvider {
    protected final Map<DependencyInfo, Supplier<?>> suppliers = new ConcurrentHashMap<>();

    /**
     * Appends another DependencyProvider to this one
     *
     * @param other Other DependencyProvider to append
     */
    public void append(DependencyProvider other) {
        suppliers.putAll(other.suppliers);
    }

    /**
     * Supply this DependencyProvider with a Supplier used when injecting
     *
     * @param _class Dependency Class
     * @param supplier Dependency Supplier
     * @param <T> Type of dependency
     */
    protected <T> void supply(Class<T> _class, Supplier<T> supplier) {
        suppliers.put(new DependencyInfo(_class), supplier);
    }

    /**
     * Supply this DependencyProvider with a Supplier used when injecting
     *
     * @param _class Dependency Class
     * @param identifier Identifier for injection
     * @param supplier Dependency Supplier
     * @param <T> Type of dependency
     */
    protected <T> void supply(Class<T> _class, String identifier, Supplier<T> supplier) {
        suppliers.put(new DependencyInfo(_class, identifier), supplier);
    }

    /**
     * Gets the Supplier matching the provided dependency class and identifier
     *
     * @param _class Dependency Class
     * @param identifier Identifier for injection
     * @return  Optional Supplier
     */
    public Optional<Supplier<?>> get(Class<?> _class, String identifier) {
        return CollectionUtil.findByKey(suppliers, i -> i.getType().equals(_class) && i.getIdentifier().equals(identifier));
    }

    /**
     * Gets the Supplier matching the provided dependency class
     *
     * @param _class Dependency Class
     * @return  Optional Supplier
     */
    public Optional<Supplier<?>> get(Class<?> _class) {
        return CollectionUtil.findByKey(suppliers, i -> i.getType().equals(_class) && i.getIdentifier().equals(Inject.EMPTY_IDENTIFIER));
    }

    /**
     * Checks if this DependencySupplier can supply a value for the provided dependency class and identifier
     *
     * @param _class Dependency Class
     * @param identifier Identifier for injection
     * @return boolean
     */
    public boolean canSupply(Class<?> _class, String identifier) {
        return get(_class, identifier).isPresent();
    }

    /**
     * Checks if this DependencySupplier can supply a value for the provided dependency class and identifier
     *
     * @param _class Dependency Class
     * @return boolean
     */
    public boolean canSupply(Class<?> _class) {
        return get(_class, Inject.EMPTY_IDENTIFIER).isPresent();
    }
}
