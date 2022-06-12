package com.alexsobiek.nexus.thread.impl;

import com.alexsobiek.nexus.thread.NexusThreadFactory;
import com.alexsobiek.nexus.thread.NexusThreadGroup;
import org.slf4j.Logger;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class ImplNexusThreadFactory implements NexusThreadFactory {
    private final Logger logger;
    private final NexusThreadGroup group;
    private final String name;
    private final Simple simple;
    private final ForkJoin forkJoin;

    public ImplNexusThreadFactory(NexusThreadGroup group, String name, Logger logger) {
        this.logger = logger;
        this.group = group;
        this.name = name;
        simple = new SimpleFactory(this);
        forkJoin = new ForkJoinFactory(this);
    }

    public ImplNexusThreadFactory(String name, Logger logger) {
        this(new ImplNexusThreadGroup(name, logger), name, logger);
    }

    public ImplNexusThreadFactory(Logger logger) {
        this("Nexus", logger);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public NexusThreadGroup getThreadGroup() {
        return group;
    }

    @Override
    public Simple getSimpleFactory() {
        return simple;
    }

    @Override
    public ForkJoin getForkJoinFactory() {
        return forkJoin;
    }

    @Override
    public int getThreadCount() {
        return group.activeCount();
    }

    protected static class SimpleFactory implements Simple {
        private final ImplNexusThreadFactory parent;

        public SimpleFactory(ImplNexusThreadFactory parent) {
            this.parent = parent;
        }

        @Override
        public Thread newThread(Runnable task) {
            Thread thread = new Thread(parent.getThreadGroup(), task);
            setThreadName(thread, parent);
            parent.logger.debug("Created new simple thread {}", thread);
            return thread;
        }
    }

    protected static class ForkJoinFactory implements ForkJoin {
        private final ForkJoinPool.ForkJoinWorkerThreadFactory defaultFactory = ForkJoinPool.defaultForkJoinWorkerThreadFactory;
        private final ImplNexusThreadFactory parent;

        public ForkJoinFactory(ImplNexusThreadFactory parent) {
            this.parent = parent;
        }

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            ForkJoinWorkerThread thread = defaultFactory.newThread(pool);
            thread.setUncaughtExceptionHandler(parent.getThreadGroup());
            setThreadName(thread, parent);
            parent.logger.debug("Created new fork join thread {}", thread);
            return thread;
        }
    }
}
