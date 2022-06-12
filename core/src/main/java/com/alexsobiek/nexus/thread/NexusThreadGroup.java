package com.alexsobiek.nexus.thread;

public abstract class NexusThreadGroup extends ThreadGroup {
    public NexusThreadGroup(String name) {
        super(name);
    }

    @Override
    public abstract void uncaughtException(Thread t, Throwable e); // force extending classes catch uncaught exceptions
}
