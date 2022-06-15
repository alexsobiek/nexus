package com.alexsobiek.nexus.inject.dependency;

import com.alexsobiek.nexus.inject.annotation.Inject;
import com.alexsobiek.nexus.util.CollectionUtil;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class DependencyProvider {
    protected final Map<DependencyInfo, Supplier<?>> suppliers = new ConcurrentHashMap<>();

    public void append(DependencyProvider other) {
        suppliers.putAll(other.suppliers);
    }

    protected <T> void supply(Class<T> _class, Supplier<T> supplier) {
        suppliers.put(new DependencyInfo(_class), supplier);
    }

    protected <T> void supply(Class<T> _class, String identifier, Supplier<T> supplier) {
        suppliers.put(new DependencyInfo(_class, identifier), supplier);
    }

    public Optional<Supplier<?>> get(Class<?> _class, String identifier) {
        return CollectionUtil.findByKey(suppliers, i -> i.getType().equals(_class) && i.getIdentifier().equals(identifier));
    }

    public Optional<Supplier<?>> get(Class<?> _class) {
        return CollectionUtil.findByKey(suppliers, i -> i.getType().equals(_class) && i.getIdentifier().equals(Inject.EMPTY_IDENTIFIER));
    }

    public boolean canSupply(Class<?> _class, String identifier) {
        return get(_class, identifier).isPresent();
    }

    public boolean canSupply(Class<?> _class) {
        return get(_class, Inject.EMPTY_IDENTIFIER).isPresent();
    }
}
