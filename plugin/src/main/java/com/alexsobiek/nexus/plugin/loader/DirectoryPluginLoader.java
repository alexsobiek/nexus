package com.alexsobiek.nexus.plugin.loader;

import com.alexsobiek.nexus.plugin.NexusPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class DirectoryPluginLoader<P extends NexusPlugin> extends CompletableFuture<Iterable<PluginContainer<P>>> {
    private final ForkJoinPool pool;
    private final JarPluginLoader<P> pluginLoader;
    private final ConcurrentLinkedQueue<File> jars;
    private final Vector<PluginContainer<P>> plugins;

    public DirectoryPluginLoader(ForkJoinPool pool, JarPluginLoader<P> pluginLoader, List<Path> directories) {
        this.pool = pool;
        this.pluginLoader = pluginLoader;
        this.jars = new ConcurrentLinkedQueue<>();
        this.plugins = new Vector<>();

        findJars(directories); // Find all jars, add them to the jars queue
        if (jars.size() > 0) load(); // Plugins found, load them
        else this.complete(plugins); // Complete with no plugins since none were found
    }

    private void findJars(List<Path> directories) {
        directories.stream().map(Path::toFile).forEach(dir -> {
            if (dir.exists()) {
                File[] files = dir.listFiles((f, n) -> n.endsWith(".jar"));
                if (files != null) jars.addAll(Arrays.stream(files).collect(Collectors.toList()));
            }
        });
    }

    private void load() {
        jars.forEach(this::loadJarAsync);
    }

    private void loadJarAsync(File file) {
        pool.execute(() -> {
            plugins.addAll(pluginLoader.loadJar(file)); // Add all plugins found in this jar
            jars.remove(file);
            if (jars.isEmpty()) this.complete(plugins); // All jars have been processed, complete future
        });
    }
}
