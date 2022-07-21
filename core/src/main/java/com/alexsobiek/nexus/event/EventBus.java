package com.alexsobiek.nexus.event;

import com.alexsobiek.nexus.Nexus;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class EventBus<E> {
    private final Executor executor;
    protected final ConcurrentHashMap<Class<? extends E>, Map<UUID, EventSubscriber<? extends E>>> subscribers;

    public EventBus(Executor executor) {
        this.executor = executor;
        subscribers = new ConcurrentHashMap<>();
    }

    public <T extends E> EventSubscriber<T> listen(Class<T> eventClass, int priority, Consumer<T> listener) {
        EventSubscriber<T> subscriber = new EventSubscriber<>(this, eventClass, listener, priority);
        if (subscribers.containsKey(eventClass)) subscribers.get(eventClass).put(subscriber.getId(), subscriber);
        else subscribers.put(eventClass, new HashMap<UUID, EventSubscriber<? extends E>>(){{
            put(subscriber.getId(), subscriber);
        }});
        return subscriber;
    }

    /**
     * Listens for an event (non prioritized)
     *
     * @param eventClass Event class of event this consumer is listening for
     * @param listener   Consumer of event
     * @param <T>        Event type
     */
    public <T extends E> EventSubscriber<T> listen(Class<T> eventClass, Consumer<T> listener) {
        return listen(eventClass, -1, listener);
    }

    @SuppressWarnings("unchecked")
    protected <T extends AsyncEvent> void postAsync(T event, Executor executor) {
        if (subscribers.containsKey(event.getClass()))
            subscribers.get(event.getClass()).values().forEach(c -> executor.execute(() -> ((EventSubscriber<T>) c).accept(event)));
    }

    /**
     * Posts an event.
     *
     * @param event    Event to post
     * @param executor Executor to use
     * @param <T>      Event type
     * @return Future - completes once all listeners have completed processing event
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> CompletableFuture<T> post(T event, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            if (event instanceof AsyncEvent) postAsync((AsyncEvent) event, executor);
            else if (subscribers.containsKey(event.getClass()))
                subscribers.get(event.getClass()).values().stream().sorted().forEach(c -> ((EventSubscriber<T>) c).accept(event));
            return event;
        }, executor);
    }

    /**
     * Posts an event.
     *
     * @param event Event to post
     * @param <T>   Event type
     * @return Future - completes once all listeners have completed processing event
     */
    public <T extends Event> CompletableFuture<T> post(T event) {
        return this.post(event, executor);
    }

    public Collection<EventSubscriber<? extends E>> subscribers(Class<? extends E> eventClass)  {
        if (subscribers.containsKey(eventClass))
            return subscribers.get(eventClass).values();
        else return Collections.emptyList();
    }
}

