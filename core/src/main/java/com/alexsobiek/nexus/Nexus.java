package com.alexsobiek.nexus;

import com.alexsobiek.nexus.event.EventBus;
import com.alexsobiek.nexus.thread.NexusThreadFactory;
import com.alexsobiek.nexus.util.Lazy;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

public class Nexus {
    protected final Logger logger;
    protected final NexusThreadFactory threadFactory;
    protected final ForkJoinPool forkJoinPool;
    private final Lazy<EventBus> eventBus;
    private final Lazy<Scheduler> scheduler;

    protected Nexus(int poolThreads, int schedulerThreads, NexusThreadFactory threadFactory, Logger logger) {
        this.threadFactory = threadFactory;
        this.logger = logger;
        this.forkJoinPool = new ForkJoinPool(poolThreads, threadFactory.getForkJoinFactory(), threadFactory.getThreadGroup(), true);
        this.eventBus = new Lazy<>(() -> new EventBus(forkJoinPool));
        this.scheduler = new Lazy<>(() -> new Scheduler(schedulerThreads, threadFactory.getSimpleFactory()));
    }

    /**
     * Runs the supplied task asynchronously
     *
     * @param task Task to run
     */
    public CompletableFuture<Void> async(Runnable task) {
        return CompletableFuture.runAsync(task, forkJoinPool);
    }

    /**
     * Runs the task asynchronously, returns the value back as a future once complete
     *
     * @param supplier T Supplier
     * @param <T>      Return type
     * @return Future
     */
    public <T> CompletableFuture<T> supply(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, forkJoinPool);
    }

    /**
     * Creates a new thread for the task to run on
     *
     * @param task Task to run
     * @return Thread
     */
    public Thread thread(Runnable task) {
        return threadFactory.getSimpleFactory().newThread(task);
    }

    /**
     * Gets the event bus.
     * EventBus object does not exist until the first time this method is called
     *
     * @return EventBus
     */
    public EventBus eventBus() {
        return eventBus.get();
    }

    /**
     * Gets the scheduler.
     * Scheduler object does not exist until the first time this method is called
     *
     * @return EventBus
     */
    public Scheduler scheduler() {
        return scheduler.get();
    }

    public static NexusBuilder builder() {
        return new NexusBuilder();
    }
}
