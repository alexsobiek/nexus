package com.alexsobiek.nexus.netty.channel;

import com.alexsobiek.nexus.netty.Connection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
@io.netty.channel.ChannelHandler.Sharable
public abstract class ChannelHandler<C extends Connection<?, ?>> extends ChannelDuplexHandler {
    private final Map<Integer, C> connections = new ConcurrentHashMap<>();

    protected Integer key(ChannelHandlerContext context) {
        return context.channel().remoteAddress().hashCode();
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {
        C conn = createConnection(context);
        if (!onConnectionActive(conn)) conn.close();
        else {
            conn.inject(context.pipeline());
            connections.put(key(context), conn);
            onConnectionAdded(conn);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) {
        if (!onHandlerAdded(context)) context.flush().close().addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        Optional<C> conn = Optional.ofNullable(connections.get(key(context)));
        conn.ifPresent(c -> c.close());
        onHandlerRemoved(context);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        if (cause instanceof ReadTimeoutException) onTimeout(context);
        else onException(context, cause);
    }

    protected abstract C createConnection(ChannelHandlerContext context);

    public abstract boolean onHandlerAdded(ChannelHandlerContext context);

    public abstract void onHandlerRemoved(ChannelHandlerContext context);

    protected abstract boolean onConnectionActive(C connection);

    protected abstract void onConnectionAdded(C connection);

    public abstract void onTimeout(ChannelHandlerContext context);

    public abstract void onException(ChannelHandlerContext context, Throwable cause);

    public Optional<C> getConnection(SocketAddress address) {
        return Optional.ofNullable(connections.get(address.hashCode()));
    }
}
