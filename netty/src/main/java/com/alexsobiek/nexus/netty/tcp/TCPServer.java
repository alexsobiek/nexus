package com.alexsobiek.nexus.netty.tcp;

import com.alexsobiek.nexus.netty.AbstractBuilder;
import com.alexsobiek.nexus.netty.channel.Pipeline;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ServerChannel;

import java.net.InetSocketAddress;

public class TCPServer extends TCPSocket<ServerChannel, ServerBootstrap> {
    public TCPServer(InetSocketAddress address, int threads, Pipeline<?> pipeline) {
        super(address, threads, pipeline);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractBuilder<TCPServer, Builder> {
        @Override
        protected TCPServer doBuild() {
            return new TCPServer(getAddress(), threads, getPipeline());
        }
    }
}
