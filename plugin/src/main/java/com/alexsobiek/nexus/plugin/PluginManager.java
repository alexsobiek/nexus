package com.alexsobiek.nexus.plugin;


import com.alexsobiek.nexus.inject.NexusInject;
import com.alexsobiek.nexus.inject.dependency.DependencyProvider;
import com.alexsobiek.nexus.plugin.exception.InvalidPluginException;
import com.alexsobiek.nexus.plugin.loader.DirectoryPluginLoader;
import com.alexsobiek.nexus.plugin.loader.JarPluginLoader;
import com.alexsobiek.nexus.plugin.loader.PluginContainer;
import com.alexsobiek.nexus.util.CollectionUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;

@Getter
@Setter(AccessLevel.PACKAGE)
public abstract class PluginManager<P extends NexusPlugin> {
    protected final HashMap<PluginContainer<P>, P> plugins = new HashMap<>();
    private JarPluginLoader<P> pluginLoader;
    private NexusInject inject;

    protected void findJarPlugins(ForkJoinPool pool, List<Path> directories) {
        DirectoryPluginLoader<P> loader = new DirectoryPluginLoader<>(pool, pluginLoader, directories);
        loader.join().forEach(this::doConstruct);
    }

    protected void doConstruct(PluginContainer<P> container) {
        if (CollectionUtil.findByKey(plugins, k -> k.equals(container)).isPresent())
            throw new RuntimeException("Attempted to load an already loaded plugin: " + container);
        else plugins.put(container, construct(container));
    }

    protected abstract P construct(PluginContainer<P> container);

    protected CompletableFuture<Optional<P>> newInstance(Class<P> mainClass, DependencyProvider provider) throws InvalidPluginException {
        return inject.construct(mainClass, provider);
    }

    protected void enableAll() {
        plugins.values().forEach(NexusPlugin::onEnable);
    }

    protected void reloadAll() {
        plugins.values().forEach(NexusPlugin::onReload);
    }

    protected void disableAll() {
        plugins.values().forEach(NexusPlugin::onDisable);
    }

    /**
     * Finds a plugin (if present/matches) based on the provided predicate
     *
     * @param predicate Predicate to use when searching
     * @return Optional plugin
     */
    public Optional<P> find(Predicate<PluginContainer<P>> predicate) {
        return CollectionUtil.findByKey(plugins, predicate);
    }

    /**
     * Gets the plugin (if present) by its main class
     *
     * @param pluginClass Main class of plugin
     * @return Optional plugin
     */
    public Optional<P> find(Class<P> pluginClass) {
        return find(p -> p.getMainClass().equals(pluginClass));
    }

    /**
     * Gets the plugin (if present) by its name
     *
     * @param name Name of plugin
     * @return Optional plugin
     */
    public Optional<P> find(String name) {
        return find(p -> p.getInfo().name().equals(name));
    }
}
