package com.alexsobiek.nexus.plugin;

public interface NexusPlugin {
    void onEnable();
    void onReload();
    void onDisable();
}
