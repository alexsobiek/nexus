package com.alexsobiek.nexus.plugin.loader;

import com.alexsobiek.nexus.plugin.NexusPlugin;
import com.alexsobiek.nexus.plugin.annotation.Plugin;
import lombok.*;

import java.nio.file.Path;
import java.util.jar.JarFile;

@ToString
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode
@RequiredArgsConstructor
public class PluginContainer<P extends NexusPlugin> {
    private final JarFile jarFile;
    private final Plugin info;
    private final Path classLocation;
    private Class<P> mainClass;
    private ClassLoader classLoader;
}
