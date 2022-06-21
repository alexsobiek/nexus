package com.alexsobiek.nexus.netty;

import com.alexsobiek.nexus.NexusLibrary;
import com.alexsobiek.nexus.lazy.Lazy;
import com.alexsobiek.nexus.netty.channel.Pipeline;
import com.alexsobiek.nexus.util.ReflectionUtil;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.AbstractEpollServerChannel;
import io.netty.channel.epoll.AbstractEpollStreamChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

@Getter
@RequiredArgsConstructor
public abstract class AbstractSocket<S extends Channel, B extends AbstractBootstrap<B, S>> extends NexusLibrary {
    protected ChannelFuture channelFuture;
    protected final InetSocketAddress address;
    protected final int threads;
    protected final Pipeline<?> pipeline;
    private final Lazy<Class<? extends Channel>> channel = new Lazy<>(this::channel);
    private final Lazy<MultithreadEventLoopGroup> nioGroup = new Lazy<>(this::nioGroup);

    protected boolean isEpoll() {
        Class<? extends Channel> cc = channel.get();
        return Epoll.isAvailable()
                && (AbstractEpollStreamChannel.class.isAssignableFrom(cc)
                || AbstractEpollServerChannel.class.isAssignableFrom(cc));
    }

    private MultithreadEventLoopGroup nioGroup() {
        return isEpoll()
                ? new EpollEventLoopGroup(threads, getThreadFactory().getSimpleFactory())
                : new NioEventLoopGroup(threads, getThreadFactory().getSimpleFactory());
    }

    protected boolean isServer() {
        return ServerChannel.class.isAssignableFrom(ReflectionUtil.getGenericParameter(getClass(), 0));
    }

    @SuppressWarnings("unchecked")
    protected B bootstrap() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Class<? extends B> bootstrapClass = (Class<? extends B>) (isServer() ? ServerBootstrap.class : Bootstrap.class);
        B bootstrap = bootstrapClass.getDeclaredConstructor().newInstance().group(nioGroup.get());
        if (isServer()) {
            ((ServerBootstrap) bootstrap).childHandler(pipeline);
            bootstrap.localAddress(address);
        } else {
            ((Bootstrap) bootstrap).remoteAddress(address);
            bootstrap.handler(pipeline);
        }
        bootstrap.channel((Class<? extends S>) channel.get());
        return bootstrap;
    }

    /**
     * Starts this socket
     *
     * @return ChannelFuture
     */
    public ChannelFuture start() {
        try {
            channelFuture = start(bootstrap());
            return channelFuture;
        } catch (Throwable t) {
            throw new RuntimeException("Failed starting socket", t);
        }
    }

    private ChannelFuture start(B bootstrap) throws InterruptedException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        ChannelFuture future;
        try {
            future = isServer() ? bootstrap.bind() : ((Bootstrap) bootstrap).connect(address);
            future.sync();
        } catch (IllegalStateException e) {
            if (Epoll.isAvailable()) {
                // An IllegalStateException might be thrown when Epoll is available but Netty fails to use it.
                // This can happen on bleeding-edge linux kernels. In this case, fallback to NioEventLoopGroup.
                nioGroup.set(new NioEventLoopGroup(threads, getThreadFactory().getSimpleFactory()));
                future = start(bootstrap());
            } else throw e;
        }
        return future;
    }

    /**
     * Stops this socket
     */
    public void stop() throws InterruptedException {
        channelFuture.channel().closeFuture().sync();
        nioGroup.get().shutdownGracefully().sync();
    }

    /**
     * Gets the {@link Channel} class this socket is using
     *
     * @return Channel class
     */
    public Class<? extends Channel> getChannel() {
        return channel.get();
    }

    /**
     * Gets the {@link MultithreadEventLoopGroup} this socket is using
     *
     * @return MultithreadEventLoopGroup
     */
    public MultithreadEventLoopGroup getNioGroup() {
        return nioGroup.get();
    }

    protected abstract Class<? extends Channel> channel();
}
