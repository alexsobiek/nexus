package com.alexsobiek.nexus.plugin;

import com.alexsobiek.nexus.NexusLibrary;
import com.alexsobiek.nexus.inject.NexusInject;
import com.alexsobiek.nexus.plugin.impl.DefaultPluginManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NPFBuilder extends NexusLibrary.Builder<NexusPluginFramework> {
    private final List<Path> directories = new ArrayList<>();
    private boolean processImmediately = false;
    private ClassLoader parentClassLoader;
    private PluginManager<?> manager;
    private NexusInject inject;

    /**
     * Adds the path to a list of directories used when searching for Jar plugins
     *
     * @param directory Directory to search
     * @return NPFBuilder
     */
    public NPFBuilder withDirectory(Path directory) {
        directories.add(directory);
        return this;
    }

    /**
     * Sets the parent class loader used when loading classes
     *
     * @param classLoader Class loader to use when loading classes
     * @return NPFBuilder
     */
    public NPFBuilder parentClassLoader(ClassLoader classLoader) {
        this.parentClassLoader = classLoader;
        return this;
    }

    /**
     * Sets the NexusInject instance to use
     *
     * @param inject NexusInject
     * @return NPFBuilder
     */
    public NPFBuilder inject(NexusInject inject) {
        this.inject = inject;
        return this;
    }

    /**
     * Tells {@link NexusPluginFramework} to process Jar files immediately on load
     *
     * @param processImmediately process Jar files immediately
     * @return NPFBuilder
     */
    public NPFBuilder processImmediately(boolean processImmediately) {
        this.processImmediately = processImmediately;
        return this;
    }

    /**
     * Sets the {@link PluginManager} object to use for managing plugins
     *
     * @param manager Plugin manager to use
     * @return NPFBuilder
     */
    public NPFBuilder manager(PluginManager<?> manager) {
        this.manager = manager;
        return this;
    }

    @Override
    public NexusLibrary.BuildableLibrary<NexusPluginFramework> build() {
        return new NexusLibrary.BuildableLibrary<NexusPluginFramework>() {
            @Override
            protected NexusPluginFramework build() {
                PluginManager<?> pm = manager != null ? manager : new DefaultPluginManager();
                pm.setInject(inject != null ? inject : getNexus().library(NexusInject.buildable()));
                return new NexusPluginFramework(
                        getExecutor(),
                        directories,
                        parentClassLoader != null ? parentClassLoader : NexusPluginFramework.class.getClassLoader(),
                        pm,
                        processImmediately
                );
            }
        };
    }
}
