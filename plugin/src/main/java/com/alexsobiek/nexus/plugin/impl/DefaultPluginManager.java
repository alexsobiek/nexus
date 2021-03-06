package com.alexsobiek.nexus.plugin.impl;

import com.alexsobiek.nexus.plugin.NexusPlugin;
import com.alexsobiek.nexus.plugin.PluginManager;
import com.alexsobiek.nexus.plugin.annotation.Plugin;
import com.alexsobiek.nexus.plugin.exception.InvalidPluginException;
import com.alexsobiek.nexus.plugin.loader.PluginContainer;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class DefaultPluginManager extends PluginManager<NexusPlugin> {
    private final DefaultDependencyProvider dependencyProvider = new DefaultDependencyProvider();

    @Override
    protected NexusPlugin construct(PluginContainer<NexusPlugin> container) {
        Plugin info = container.getInfo();
        NexusPlugin p;
        try {
            Optional<NexusPlugin> opt = newInstance(container.getMainClass(), dependencyProvider).get();
            if (opt.isPresent()) p = opt.get();
            else throw new InvalidPluginException("Failed to call main class " + container.getMainClass());
        } catch (InvalidPluginException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        p.onEnable();
        return p;
    }
}
