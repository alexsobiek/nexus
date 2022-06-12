package com.alexsobiek.nexus.thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;

public interface NexusThreadFactory {
    interface IFactory {
        default void setThreadName(Thread thread, NexusThreadFactory factory) {
            thread.setName(String.format("%s-%d", factory.getPrefix(), factory.getThreadCount() + 1));
        }
    }

    interface Simple extends IFactory, ThreadFactory {
    }

    interface ForkJoin extends IFactory, ForkJoinPool.ForkJoinWorkerThreadFactory {
    }

    String getPrefix();

    ThreadGroup getThreadGroup();

    Simple getSimpleFactory();

    ForkJoin getForkJoinFactory();

    int getThreadCount();
}
