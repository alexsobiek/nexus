package com.alexsobiek.nexus.util;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class CollectionUtil {
    public static <E> Optional<E> find(Collection<E> collection, Predicate<E> predicate) {
        return collection.stream().filter(predicate).findFirst();
    }

    public static <K, V> Optional<V> findByKey(Map<K, V> map, Predicate<K> predicate) {
        return find(map.keySet(), predicate).map(map::get);
    }

    public static <K, V> Optional<V> findByValue(Map<K, V> map, Predicate<V> predicate) {
        return find(map.values(), predicate);
    }
}