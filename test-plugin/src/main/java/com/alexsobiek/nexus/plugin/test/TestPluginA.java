package com.alexsobiek.nexus.plugin.test;

import com.alexsobiek.nexus.plugin.NexusPlugin;
import com.alexsobiek.nexus.plugin.annotation.Plugin;

@Plugin(
        name = "Test Plugin A",
        description = "Test plugin description",
        version = "1.0",
        authors = {"Alex Sobiek", "Nexus Contributors"}
)
public class TestPluginA implements NexusPlugin {
    public boolean enabled = false; // Used to test if this plugin has been loaded properly

    @Override
    public void onEnable() {
        enabled = true;
        System.out.println("Test Plugin A loaded");
    }

    @Override
    public void onReload() {
        System.out.println("Test Plugin A reloaded");
    }

    @Override
    public void onDisable() {
        System.out.println("Test Plugin A disabled");
    }
}