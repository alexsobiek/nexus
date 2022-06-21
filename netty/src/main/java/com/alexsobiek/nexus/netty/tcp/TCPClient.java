package com.alexsobiek.nexus.netty.tcp;

import com.alexsobiek.nexus.netty.AbstractBuilder;
import com.alexsobiek.nexus.netty.channel.Pipeline;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class TCPClient extends TCPSocket<Channel, Bootstrap> {

    public TCPClient(InetSocketAddress address, int threads, Pipeline<?> pipeline) {
        super(address, threads, pipeline);
    }

    static class Builder extends AbstractBuilder<TCPClient, Builder> {
        @Override
        protected TCPClient doBuild() {
            return new TCPClient(address(), threads, pipeline());
        }
    }
}
