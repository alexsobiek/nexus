package com.alexsobiek.nexus.tcp.server;

import com.alexsobiek.nexus.tcp.TCPSocket;
import com.alexsobiek.nexus.tcp.channel.Pipeline;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ServerChannel;

import java.net.InetSocketAddress;

public class TCPServer extends TCPSocket<ServerChannel, ServerBootstrap> {
    public TCPServer(InetSocketAddress address, int threads, Pipeline<?> pipeline) {
        super(address, threads, pipeline);
    }
}
