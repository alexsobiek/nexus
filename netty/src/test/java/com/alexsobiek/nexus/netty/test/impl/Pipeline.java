package com.alexsobiek.nexus.netty.test.impl;

import com.alexsobiek.nexus.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;

public class Pipeline extends com.alexsobiek.nexus.netty.channel.Pipeline<Connection> {
    public Pipeline(ChannelHandler<Connection> channelHandler) {
        super(channelHandler, 30);
    }

    @Override
    protected void init(SocketChannel channel) {

    }
}
