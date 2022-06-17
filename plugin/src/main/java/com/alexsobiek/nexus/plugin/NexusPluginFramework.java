package com.alexsobiek.nexus.plugin;

import com.alexsobiek.nexus.NexusLibrary;
import com.alexsobiek.nexus.plugin.loader.JarPluginLoader;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class NexusPluginFramework extends NexusLibrary {
    private final ForkJoinPool pool;
    private final List<Path> directories;
    private final PluginManager<?> manager;

    protected NexusPluginFramework(ForkJoinPool pool, List<Path> directories, ClassLoader classLoader, PluginManager<?> manager, boolean processImmediately) {
        this.directories = directories;
        this.manager = manager;
        this.pool = pool;
        manager.setPluginLoader(new JarPluginLoader<>(classLoader));
        if (processImmediately) findAndLoadPlugins();
    }

    /**
     * Looks for plugins in the specified directories and loads them
     */
    public void findAndLoadPlugins() {
        manager.findJarPlugins(pool, directories);
    }

    /**
     * Gets the plugin manager
     *
     * @return PluginManager
     */
    public PluginManager<?> getPluginManager() {
        return manager;
    }

    public static NPFBuilder builder() {
        return new NPFBuilder();
    }


}
