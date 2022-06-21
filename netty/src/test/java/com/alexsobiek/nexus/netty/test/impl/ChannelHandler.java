package com.alexsobiek.nexus.netty.test.impl;

import com.alexsobiek.nexus.netty.test.TestServerClient;
import io.netty.channel.ChannelHandlerContext;

public class ChannelHandler extends com.alexsobiek.nexus.netty.channel.ChannelHandler<Connection> {
    public enum Type {
        CLIENT, SERVER
    }

    private final Type type;

    public ChannelHandler(Type type) {
        this.type = type;
    }

    @Override
    protected Connection createConnection(ChannelHandlerContext context) {
        return new Connection(context, type);
    }

    @Override
    public boolean onHandlerAdded(ChannelHandlerContext context) {
        return true;
    }

    @Override
    public void onHandlerRemoved(ChannelHandlerContext context) {

    }

    @Override
    protected boolean onConnectionActive(Connection connection) {
        return true;
    }

    @Override
    protected void onConnectionAdded(Connection connection) {
        TestServerClient.nexus.eventBus().post(new ConnectionAddedEvent(connection));
    }

    @Override
    public void onTimeout(ChannelHandlerContext context) {

    }

    @Override
    public void onException(ChannelHandlerContext context, Throwable cause) {

    }

    public Type getType() {
        return type;
    }
}
