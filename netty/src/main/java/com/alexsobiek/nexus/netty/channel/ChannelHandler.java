package com.alexsobiek.nexus.netty.channel;

import com.alexsobiek.nexus.netty.Connection;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
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

    @Override
    public void channelActive(ChannelHandlerContext context) {
        C conn = createConnection(context);
        if (!onConnectionActive(createConnection(context))) conn.close();
        else {
            conn.inject(context.pipeline());
            connections.put(conn.getRemoteAddress().hashCode(), conn);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) {
        if (!onHandlerAdded(context)) context.flush().close().addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Optional<C> conn = Optional.ofNullable(connections.get(ctx.channel().remoteAddress().hashCode()));
        conn.ifPresent(c -> c.close());
        onHandlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ReadTimeoutException) onTimeout(ctx);
        else onException(ctx, cause);
    }

    protected abstract C createConnection(ChannelHandlerContext context);

    public abstract boolean onHandlerAdded(ChannelHandlerContext context);

    public abstract void onHandlerRemoved(ChannelHandlerContext context);

    protected abstract boolean onConnectionActive(C connection);

    public abstract void onTimeout(ChannelHandlerContext context);

    public abstract void onException(ChannelHandlerContext context, Throwable cause);

    public Optional<C> getConnection(SocketAddress remoteAddress) {
        return Optional.ofNullable(connections.get(remoteAddress.hashCode()));
    }
}
