package com.alexsobiek.nexus.tcp;

import com.alexsobiek.nexus.NexusLibrary;
import com.alexsobiek.nexus.lazy.Lazy;
import com.alexsobiek.nexus.tcp.channel.Pipeline;
import com.alexsobiek.nexus.tcp.server.TCPServer;
import com.alexsobiek.nexus.util.ReflectionUtil;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

@RequiredArgsConstructor
public class TCPSocket<S extends TCPSocket<?, ?, ?>, B extends AbstractBootstrap<B, ?>, C extends Connection<?, ?>> extends NexusLibrary {
    protected final InetSocketAddress address;
    protected final int threads;
    protected final Pipeline<C> pipeline;
    // protected final Channel channel;
    protected final Lazy<Class<? extends Channel>> channel = new Lazy<>(() -> {
        Class<S> cc = (Class<S>) ReflectionUtil.getGenericParameter(getClass(), 0);
        return Epoll.isAvailable()
                ? isServer(cc)
                ? EpollServerSocketChannel.class
                : EpollSocketChannel.class
                : isServer(cc)
                ? NioServerSocketChannel.class
                : NioSocketChannel.class;
    });
    protected final Lazy<MultithreadEventLoopGroup> nioGroup = new Lazy<>(() -> {
        Class<? extends Channel> cc = channel.get();
        return Epoll.isAvailable() && (cc.equals(EpollServerSocketChannel.class) || cc.equals(EpollSocketChannel.class))
                ? new EpollEventLoopGroup(threads, getThreadFactory().getSimpleFactory())
                : new NioEventLoopGroup(threads, getThreadFactory().getSimpleFactory());
    });

    private boolean isServer(Class<S> socketClass) {
        return socketClass.isAssignableFrom(TCPServer.class);
    }

    private boolean isServer(B bootstrap) {
        return bootstrap instanceof ServerBootstrap;
    }

    protected <A extends Channel> B bootstrap(Class<? extends B> bootstrapClass)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        B bootstrap = bootstrapClass.getDeclaredConstructor().newInstance().group(nioGroup.get());
        if (isServer(bootstrap)) {
            ((ServerBootstrap) bootstrap).childHandler(pipeline);
            bootstrap.localAddress(address);
        } else {
            ((Bootstrap) bootstrap).remoteAddress(address);
            bootstrap.handler(pipeline);
        }
        bootstrap.channel((Channel) channel.get());

        return bootstrap;
    }

    private ChannelFuture start(B bootstrap) throws InterruptedException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        ChannelFuture future = null;
        try {
            if (isServer(bootstrap)) bootstrap.bind().sync();
            else ((Bootstrap) bootstrap).connect(address).sync();
        } catch (IllegalStateException e) {
            // An IllegalStateException might be thrown when Epoll is available but Netty fails to use it.
            // This can happen on bleeding-edge linux kernels. In this case, fallback to NioEventLoopGroup.
            nioGroup.set(new NioEventLoopGroup(threads, getThreadFactory().getSimpleFactory()));
        }
    }
}
