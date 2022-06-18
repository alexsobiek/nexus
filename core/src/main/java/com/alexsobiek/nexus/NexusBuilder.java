package com.alexsobiek.nexus;

import com.alexsobiek.nexus.thread.NexusThreadFactory;
import com.alexsobiek.nexus.thread.impl.ImplNexusThreadFactory;

public class NexusBuilder {
    private int poolThreads = -1;
    private int schedulerThreads = -1;
    private NexusThreadFactory threadFactory;

    /**
     * Sets the amount of threads available for the common pool.
     * If not set, all threads available will be used.
     * Does not affect the amount of threads created with {@link Nexus#async(Runnable)}
     *
     * @param threads Threads for common pool
     * @return NexusBuilder
     */
    public NexusBuilder threads(int threads) {
        this.poolThreads = threads;
        return this;
    }


    /**
     * Sets the amount of threads to use for the {@link Scheduler}.
     * If not, the same amount of threads set with {@link NexusBuilder#threads(int)} will be used.
     * If {@link Nexus#scheduler()} is never called, no threads will be created.
     *
     * @param threads Number of threads to use
     * @return NexusBuilder
     */
    public NexusBuilder schedulerThreads(int threads) {
        this.schedulerThreads = threads;
        return this;
    }

    /**
     * Sets the thread factory to be used when creating threads
     *
     * @param factory Thread factory for creating threads
     * @return NexusBuilder
     */
    public NexusBuilder threadFactory(NexusThreadFactory factory) {
        this.threadFactory = factory;
        return this;
    }

    // Methods use for building
    private int pt() {
        return poolThreads == -1
                ? Runtime.getRuntime().availableProcessors()
                : poolThreads;
    }

    private int st(int poolThreads) {
        return schedulerThreads == -1
                ? poolThreads
                : schedulerThreads;
    }

    private NexusThreadFactory tf() {
        return threadFactory == null
                ? new ImplNexusThreadFactory()
                : threadFactory;
    }

    public Nexus build() {
        int pt = pt();
        int st = st(pt);
        Nexus nexus = new Nexus(pt, st, tf());
        return nexus;
    }
}
