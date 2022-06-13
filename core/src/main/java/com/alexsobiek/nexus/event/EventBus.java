package com.alexsobiek.nexus.event;

import com.alexsobiek.nexus.Nexus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class EventBus {
    private final Executor executor;
    private final ConcurrentHashMap<Class<? extends Event>, List<EventConsumer<Event>>> eventListeners;

    public EventBus(Executor executor) {
        this.executor = executor;
        eventListeners = new ConcurrentHashMap<>();
    }

    /**
     * Listens for an event with priority
     *
     * @param eventClass Event class of event this consumer is listening for
     * @param priority   This consumers priority
     * @param listener   Consumer of event
     * @param <T>        Event type
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void listen(Class<T> eventClass, int priority, Consumer<T> listener) {
        EventConsumer<T> consumer = new EventConsumer<>(listener, priority);
        if (eventListeners.containsKey(eventClass)) eventListeners.get(eventClass).add((EventConsumer<Event>) consumer);
        else eventListeners.put(eventClass, new ArrayList<EventConsumer<Event>>() {{
            add((EventConsumer<Event>) consumer);
        }});
    }

    /**
     * Listens for an event (non prioritized)
     *
     * @param eventClass Event class of event this consumer is listening for
     * @param listener   Consumer of event
     * @param <T>        Event type
     */
    public <T extends Event> void listen(Class<T> eventClass, Consumer<T> listener) {
        listen(eventClass, -1, listener);
    }

    protected <T extends AsyncEvent> void postAsync(T event, Executor executor) {
        if (eventListeners.containsKey(event.getClass()))
            eventListeners.get(event.getClass()).forEach(c -> executor.execute(() -> c.accept(event)));
    }

    /**
     * Posts an event.
     *
     * @param event    Event to post
     * @param executor Executor to use
     * @param <T>      Event type
     * @return Future - completes once all listeners have completed processing event
     */
    public <T extends Event> CompletableFuture<T> post(T event, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            if (event instanceof AsyncEvent) postAsync((AsyncEvent) event, executor);
            else if (eventListeners.containsKey(event.getClass()))
                eventListeners.get(event.getClass()).stream().sorted().forEach(consumer -> consumer.accept(event));
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
}

