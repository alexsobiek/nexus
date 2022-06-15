package com.alexsobiek.nexus.inject;

import com.alexsobiek.nexus.NexusLibrary;
import com.alexsobiek.nexus.inject.annotation.Inject;
import com.alexsobiek.nexus.inject.dependency.DependencyProvider;
import com.alexsobiek.nexus.inject.exception.InjectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class NexusInject extends NexusLibrary {
    public <T> CompletableFuture<Optional<T>> construct(Class<T> _class, DependencyProvider provider) {
        return getNexus().supply(() -> {
            Map<Field, Supplier<?>> suppliers = new HashMap<>();
            for (Field field : _class.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Inject annotation = field.getDeclaredAnnotation(Inject.class);
                    Optional<Supplier<?>> supplier = provider.get(field.getType(), annotation.identifier());

                    if (supplier.isPresent()) suppliers.put(field, supplier.get());
                    else {
                        InjectionException.runtime(String.format("No supplier for field %s in class %s", field, _class));
                        return Optional.empty();
                    }
                }
            }

            try {
                Constructor<T> constructor = _class.getDeclaredConstructor();
                constructor.setAccessible(true);
                T instance = constructor.newInstance();

                for (Map.Entry<Field, Supplier<?>> entry : suppliers.entrySet()) {
                    Field field = entry.getKey();
                    field.setAccessible(true);
                    try {
                        field.set(instance, entry.getValue().get());
                    } catch (IllegalAccessException e) {
                        InjectionException.runtime(String.format("Failed setting field %s in class %s", field, _class), e);
                        return Optional.empty();
                    }
                }
                return Optional.of(instance);
            } catch (Throwable t) {
                InjectionException.runtime("Failed constructing " + _class, t);
                return Optional.empty();
            }
        });
    }


    public static BuildableLibrary<NexusInject> buildable() {
        return new NexusLibrary.BuildableLibrary<NexusInject>() {
            @Override
            protected NexusInject build() {
                return new NexusInject();
            }
        };
    }
}
