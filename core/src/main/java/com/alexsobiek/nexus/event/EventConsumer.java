package com.alexsobiek.nexus.event;

import java.util.function.Consumer;

public class EventConsumer<T extends Event> implements Consumer<T>, Comparable<EventConsumer<T>> {
    private final Consumer<T> parent;
    private int priority = -1;

    public EventConsumer(Consumer<T> parent, int priority) {
        this.parent = parent;
        this.priority = priority;
    }

    public EventConsumer(Consumer<T> parent) {
        this.parent = parent;
    }

    @Override
    public int compareTo(EventConsumer<T> o) {
        if (priority == o.priority) return 0; // Edge case since priority is -1 by default
        else if (priority < 0) return Math.abs(priority) + o.priority; // Events with negative priority should be last
        else return o.priority - priority;
    }

    @Override
    public void accept(T t) {
        parent.accept(t);
    }
}
