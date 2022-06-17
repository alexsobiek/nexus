package com.alexsobiek.nexus.plugin.loader;

import com.alexsobiek.nexus.plugin.NexusPlugin;
import com.alexsobiek.nexus.plugin.annotation.Plugin;
import com.alexsobiek.nexus.plugin.asm.AnnotationFinder;
import com.alexsobiek.nexus.plugin.util.JarUtil;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.jar.JarFile;

public class JarPluginLoader<P extends NexusPlugin> {
    private static final String annotationDescriptor = "L" + Plugin.class.getTypeName().replace(".", "/") + ";";
    private final ClassLoader parentClassLoader;
    private final Map<JarFile, PluginClassLoader> loaded;

    public JarPluginLoader(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
        this.loaded = new ConcurrentHashMap<>();
    }

    /**
     * Attempts to load plugins from the provided jar file
     *
     * @param file File to load
     * @return List of plugin containers
     */
    public List<PluginContainer<P>> loadJar(File file) {
        List<PluginContainer<P>> plugins = new ArrayList<>();
        try {
            JarFile jar = new JarFile(file);
            JarUtil.forEachClassFile(file, path -> {
                ifMainClass(path, plugin -> {
                    PluginContainer<P> container = new PluginContainer<>(jar, plugin, path);
                    PluginClassLoader loader;

                    if (loaded.containsKey(jar)) loader = loaded.get(jar);
                    else {
                        loader = new PluginClassLoader(parentClassLoader);
                        loader.addJar(file);
                        loader.loadClasses(jar);
                        loaded.put(jar, loader);
                    }

                    container.setClassLoader(loader);
                    try {
                        Class<?> mainClass = Class.forName(pathToClassName(path), false, loader);
                        container.setMainClass((Class<P>) mainClass);
                        plugins.add(container);
                    } catch (Throwable t) {
                        throw new RuntimeException("Failed loading main class for plugin " + container, t);
                    }
                });
            });
            return plugins;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void ifMainClass(Path path, Consumer<Plugin> consumer) {
        try {
            InputStream is = Files.newInputStream(path);
            ClassReader reader = new ClassReader(is);
            AnnotationFinder finder = new AnnotationFinder(s -> s.equals(annotationDescriptor));
            reader.accept(finder, 0);

            if (finder.hasAnnotation() && finder.getFields().isPresent()) {
                Map<String, Object> fields = finder.getFields().get();
                consumer.accept(new Plugin() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Plugin.class;
                    }

                    @Override
                    public String name() {
                        return (String) fields.getOrDefault("name", "unknown");
                    }

                    @Override
                    public String description() {
                        return (String) fields.getOrDefault("description", "unknown");
                    }

                    @Override
                    public String version() {
                        return (String) fields.getOrDefault("version", "unknown");
                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public String[] authors() {
                        if (fields.containsKey("authors"))
                            return ((List<String>) fields.get("authors")).toArray(new String[]{});
                        else return new String[]{"unknown"};
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String pathToClassName(Path path) {
        return path.toString().replace('/', '.').replace(".class", "");
    }
}
