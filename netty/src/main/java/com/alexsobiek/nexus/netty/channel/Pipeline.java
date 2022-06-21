package com.alexsobiek.nexus.netty.channel;

import com.alexsobiek.nexus.netty.Connection;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Pipeline<C extends Connection<?, ?>> extends ChannelInitializer<SocketChannel> {
    private final ChannelHandler<C> channelHandler;
    private final int timeoutSeconds;
    protected String channelHandlerName = "channelHandler";
    protected String timeoutName = "timeout";

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        init(channel);

        pipeline.addLast(channelHandlerName, channelHandler);
        pipeline.addLast(timeoutName, new ReadTimeoutHandler(timeoutSeconds));
    }


    /**
     * This method will be called from once the {@link SocketChannel} is registered.
     * @param channel The SocketChannel for this pipeline
     */
    protected abstract void init(SocketChannel channel);
}
