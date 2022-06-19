package com.alexsobiek.nexus.tcp.server;

import com.alexsobiek.nexus.tcp.TCPSocket;
import com.alexsobiek.nexus.tcp.channel.Pipeline;
import io.netty.channel.MultithreadEventLoopGroup;

import java.net.InetSocketAddress;

public class TCPServer extends TCPSocket {

    public TCPServer(InetSocketAddress address, MultithreadEventLoopGroup nioGroup, Pipeline pipeline) {
        super(address, nioGroup, pipeline);
    }
}
