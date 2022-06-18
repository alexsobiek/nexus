package com.alexsobiek.nexus.thread.impl;

import com.alexsobiek.nexus.thread.NexusThreadFactory;
import com.alexsobiek.nexus.thread.NexusThreadGroup;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class ImplNexusThreadFactory implements NexusThreadFactory {
    private final NexusThreadGroup group;
    private final String name;
    private final Simple simple;
    private final ForkJoin forkJoin;

    public ImplNexusThreadFactory(NexusThreadGroup group, String name) {
        this.group = group;
        this.name = name;
        simple = new SimpleFactory(this);
        forkJoin = new ForkJoinFactory(this);
    }

    public ImplNexusThreadFactory(String name) {
        this(new ImplNexusThreadGroup(name), name);
    }

    public ImplNexusThreadFactory() {
        this("Nexus");
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
            return thread;
        }
    }
}
