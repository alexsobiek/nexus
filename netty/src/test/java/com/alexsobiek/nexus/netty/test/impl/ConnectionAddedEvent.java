package com.alexsobiek.nexus.netty.test.impl;

import com.alexsobiek.nexus.event.Event;

public class ConnectionAddedEvent implements Event {
    private final Connection connection;

    public ConnectionAddedEvent(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
