package com.alexsobiek.nexus.plugin.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

public class PluginClassLoader extends URLClassLoader {
    public PluginClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }

    protected void addJar(File file) {
        try {
            addURL(file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadClasses(JarFile jarFile) {
        jarFile.stream() // Load all the classes in the plugin
                .filter(e -> e.getName().endsWith(".class") && !e.getName().endsWith("module-info.class"))
                .map(je -> je.getName().replace("/", ".").replace(".class", ""))
                .forEach(str -> {
                    try {
                        loadClass(str);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
