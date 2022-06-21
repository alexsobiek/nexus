package com.alexsobiek.nexus.netty.tcp;

import com.alexsobiek.nexus.netty.AbstractSocket;
import com.alexsobiek.nexus.netty.channel.Pipeline;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public abstract class TCPSocket<S extends Channel, B extends AbstractBootstrap<B, S>> extends AbstractSocket<S, B> {
    public TCPSocket(InetSocketAddress address, int threads, Pipeline<?> pipeline) {
        super(address, threads, pipeline);
    }

    protected Class<? extends Channel> channel() {
        return Epoll.isAvailable()
                ? isServer()
                ? EpollServerSocketChannel.class
                : EpollSocketChannel.class
                : isServer()
                ? NioServerSocketChannel.class
                : NioSocketChannel.class;
    }
}
