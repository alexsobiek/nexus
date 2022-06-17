package com.alexsobiek.nexus.plugin.tests;

import com.alexsobiek.nexus.Nexus;
import com.alexsobiek.nexus.plugin.NexusPlugin;
import com.alexsobiek.nexus.plugin.NexusPluginFramework;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Test Plugin Loading")
public class TestPluginLoading {

    @Test
    @SuppressWarnings("unchecked")
    void testLoading() {
        Nexus nexus = Nexus.builder().build();

        NexusPluginFramework framework = nexus.library(NexusPluginFramework.builder()
                .processImmediately(true)
                .withDirectory(Paths.get("src", "main", "resources"))
                .build());

        Optional<NexusPlugin> pluginA = (Optional<NexusPlugin>) framework.getPluginManager().find("Test Plugin A");
        Optional<NexusPlugin> pluginB = (Optional<NexusPlugin>) framework.getPluginManager().find("Test Plugin B");

        assertTrue(pluginA.isPresent());
        assertTrue(pluginB.isPresent());
        assertTrue(isEnabled(pluginA.get()));
        assertTrue(isEnabled(pluginB.get()));
    }

    public boolean isEnabled(NexusPlugin plugin) {
        try {
            Field enabledF = plugin.getClass().getDeclaredField("enabled");
            enabledF.setAccessible(true);
            return enabledF.getBoolean(plugin);
        } catch (Throwable t) {
            throw new RuntimeException("Failed checking if plugin is enabled", t);
        }
    }
}
