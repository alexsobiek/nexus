package com.alexsobiek.nexus.plugin.test;

import com.alexsobiek.nexus.plugin.NexusPlugin;
import com.alexsobiek.nexus.plugin.annotation.Plugin;

@Plugin(
        name = "Test Plugin B",
        description = "Test plugin description",
        version = "1.0",
        authors = {"Alex Sobiek", "Nexus Contributors"}
)
public class TestPluginB implements NexusPlugin {
    public boolean enabled = false; // Used to test if this plugin has been loaded properly

    @Override
    public void onEnable() {
        enabled = true;
        System.out.println("Test Plugin B loaded");
    }

    @Override
    public void onReload() {
        System.out.println("Test Plugin B reloaded");
    }

    @Override
    public void onDisable() {
        System.out.println("Test Plugin B disabled");
    }
}
