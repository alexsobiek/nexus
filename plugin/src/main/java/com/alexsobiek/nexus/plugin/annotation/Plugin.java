package com.alexsobiek.nexus.plugin.annotation;

public @interface Plugin {
    String name();

    String description();

    String version();

    String[] authors();
}
