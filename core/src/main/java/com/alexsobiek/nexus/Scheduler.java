package com.alexsobiek.nexus;

import com.alexsobiek.nexus.thread.NexusThreadFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class Scheduler {
    private final ScheduledThreadPoolExecutor scheduler;
    private final Map<UUID, ScheduledFuture<?>> map;

    public Scheduler(int threads, NexusThreadFactory.Simple threadFactory) {
        scheduler = new ScheduledThreadPoolExecutor(threads, threadFactory);
        map = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new delayed task
     * See {@link ScheduledExecutorService#schedule(Runnable, long, TimeUnit)}
     * @param task Task to run
     * @param delay Start delay
     * @param timeUnit TimeUnit for delay
     * @return Task UUID
     */
    public UUID schedule(Runnable task, long delay, TimeUnit timeUnit) {
        UUID id = UUID.randomUUID();
        map.put(id, scheduler.schedule(task, delay, timeUnit));
        return id;
    }

    /**
     * Creates a new delayed task
     * @param task Task to run
     * @param delay Start delay (in milliseconds)
     * @return Task UUID
     */
    public UUID schedule(Runnable task, long delay) {
        return schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Submits a periodic action that becomes enabled first after the given initial delay,
     * and subsequently with the given delay between the termination of one execution and the commencement of the next.
     * See {@link ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)}
     * @param task Task to run
     * @param delay Start delay
     * @param period Period between runs
     * @param timeUnit TimeUnit for delay & period
     * @return Task UUID
     */
    public UUID scheduleWithFixedDelay(Runnable task, long delay, long period, TimeUnit timeUnit) {
        UUID id = UUID.randomUUID();
        map.put(id, scheduler.scheduleWithFixedDelay(task, delay, period, timeUnit));
        return id;
    }

    /**
     * Submits a periodic action that becomes enabled first after the given initial delay,
     * and subsequently with the given delay between the termination of one execution and the commencement of the next.
     * See {@link ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)}
     * @param task Task to run
     * @param delay Start delay (in milliseconds)
     * @param period Period between runs (in milliseconds)
     * @return Task UUID
     */
    public UUID schedule(Runnable task, long delay, long period) {
        return scheduleWithFixedDelay(task, delay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * Submits a periodic action that becomes enabled first after the
     * given initial delay, and subsequently with the given period;
     * that is, executions will commence after. See {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     * @param task Task to run
     * @param delay Start delay
     * @param period Period between runs
     * @param timeUnit TimeUnit for delay & period
     * @return Task UUID
     */
    public UUID scheduleAtFixedRate(Runnable task, long delay, long period, TimeUnit timeUnit) {
        UUID id = UUID.randomUUID();
        map.put(id, scheduler.scheduleAtFixedRate(task, delay, period, timeUnit));
        return id;
    }

    /**
     * Submits a periodic action that becomes enabled first after the
     * given initial delay, and subsequently with the given period;
     * that is, executions will commence after. See {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
     * @param task Task to run
     * @param delay Start delay (in milliseconds)
     * @param period Period between runs (in milliseconds)
     * @return Task UUID
     */
    public UUID scheduleAtFixedRate(Runnable task, long delay, long period) {
        return scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * Cancels the scheduled task with the provided ID (if exists)
     * @param id Task UUID
     */
    public void cancel(UUID id) {
        ScheduledFuture<?> removed = map.remove(id);
        if (removed != null) removed.cancel(true);
    }
}