package com.alexsobiek.nexus.thread.impl;

import com.alexsobiek.nexus.thread.NexusThreadGroup;

public class ImplNexusThreadGroup extends NexusThreadGroup {

    public ImplNexusThreadGroup(String name) {
        super(name);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        throw new RuntimeException("Uncaught exception from thread " + t, e);
    }
}
