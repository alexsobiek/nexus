package com.alexsobiek.nexus.thread.impl;

import com.alexsobiek.nexus.thread.NexusThreadGroup;
import org.slf4j.Logger;

public class ImplNexusThreadGroup extends NexusThreadGroup {
    private final Logger logger;

    public ImplNexusThreadGroup(String name, Logger logger) {
        super(name);
        this.logger = logger;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }
}
