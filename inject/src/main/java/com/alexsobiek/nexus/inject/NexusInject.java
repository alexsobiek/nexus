package com.alexsobiek.nexus.inject;

import com.alexsobiek.nexus.NexusLibrary;

public class NexusInject extends NexusLibrary {

    public static NexusInjectBuilder builder() {
        return new NexusInjectBuilder();
    }
}
