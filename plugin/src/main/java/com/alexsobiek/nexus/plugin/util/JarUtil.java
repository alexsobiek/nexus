package com.alexsobiek.nexus.plugin.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class JarUtil {
    public static Stream<JarEntry> classFileStream(JarFile jarFile) {
        return jarFile.stream().filter(e -> e.getName().endsWith(".class") && !e.getName().endsWith("module-info.class"));
    }

    public static void forEachClass(JarFile jarFile, Consumer<String> consumer) {
        classFileStream(jarFile)
                .map(je -> je.getName().replace("/", ".").replace(".class", ""))
                .forEach(consumer);
    }

    public static void forEachClassFile(File jarFile, Consumer<Path> consumer) {
        try {
            FileSystem fs = jarFS(jarFile);
            JarFile jar = new JarFile(jarFile);
            classFileStream(jar)
                    .map(je -> fs.getPath(je.getName()))
                    .forEach(consumer);
            fs.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileSystem jarFS(File file) {
        try {
            URI uri = new URI("jar", file.toURI().toString(), null);
            return FileSystems.newFileSystem(uri, new HashMap<String, String>() {{
                put("create", "true");
            }});
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
