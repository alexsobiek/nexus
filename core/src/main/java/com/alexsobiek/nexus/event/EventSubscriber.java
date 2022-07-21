package com.alexsobiek.nexus.event;

import java.util.UUID;
import java.util.function.Consumer;

public class EventSubscriber<E> implements Consumer<E>, Comparable<EventSubscriber<E>>, AutoCloseable {
    private final UUID id = UUID.randomUUID();
    private final EventBus<?> bus;
    private final Class<E> eventClass;
    private final Consumer<E> parent;
    private int priority;

    public EventSubscriber(EventBus<?> bus, Class<E> eventClass, Consumer<E> parent, int priority) {
        this.bus = bus;
        this.eventClass = eventClass;
        this.parent = parent;
        this.priority = priority;
    }

    public EventSubscriber(EventBus<?> bus, Class<E> eventClass, Consumer<E> parent) {
        this(bus, eventClass, parent, -1);
    }

    public UUID getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(EventSubscriber<E> o) {
        if (priority == o.priority) return 0; // Edge case since priority is -1 by default
        else if (priority < 0) return Math.abs(priority) + o.priority; // Events with negative priority should be last
        else return o.priority - priority;
    }

    @Override
    public void accept(E event) {
        parent.accept(event);
    }

    @Override
    public void close() {
        System.out.println("Closing " + id);
        if (bus.subscribers.containsKey(eventClass))
            bus.subscribers.get(eventClass).remove(id);
    }
}
