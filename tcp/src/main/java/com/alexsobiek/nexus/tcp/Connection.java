package com.alexsobiek.nexus.tcp;

import com.alexsobiek.nexus.tcp.proto.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

/**
 * Abstract Connection class
 *
 * @param <B> Buffer type
 * @param <P> Packet type
 */
@RequiredArgsConstructor
public abstract class Connection<B extends ByteBuf, P extends Packet<B>> {
    @Getter
    protected final ChannelHandlerContext context;
    @Getter
    private boolean active;
    protected String decoderName = "decoder";
    protected String encoderName = "encoder";
    protected String packetHandlerName = "packet_handler";

    public final MessageToByteEncoder<P> encoder = new MessageToByteEncoder<P>() {
        @Override
        protected void encode(ChannelHandlerContext ctx, P msg, ByteBuf out) throws Exception {
            if (checkContext(ctx)) Connection.this.encode(msg, out);
        }
    };

    public final ByteToMessageDecoder decoder = new ByteToMessageDecoder() {
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if (checkContext(ctx)) Connection.this.decode(in, out);
        }
    };

    public final MessageToMessageCodec<P, P> packetHandler = new MessageToMessageCodec<P, P>() {
        @Override
        protected void encode(ChannelHandlerContext ctx, P msg, List<Object> out) throws Exception {
            if (checkContext(ctx)) outgoingPacket(msg).ifPresent(out::add);

        }

        @Override
        protected void decode(ChannelHandlerContext ctx, P msg, List<Object> out) throws Exception {
            if (checkContext(ctx)) incomingPacket(msg); // End of the line, handle this packet
        }
    };



    /**
     * Injects this connection's codec into the pipeline
     *
     * @param pipeline Pipeline to inject to
     */
    public void inject(ChannelPipeline pipeline) {
        if (!pipeline.equals(context.pipeline())) throw new RuntimeException("Mismatched ChannelPipeline object");
        pipeline.addLast(decoderName, decoder);
        pipeline.addLast(encoderName, encoder);
        pipeline.addLast(packetHandlerName, packetHandler);
    }

    protected boolean checkContext(ChannelHandlerContext ctx) {
        return context.channel().equals(ctx.channel());
    }

    /**
     * Returns the remote address of this connection
     *
     * @return InetSocketAddress
     */
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) context.channel().remoteAddress();
    }

    /**
     * Returns the local address of this connection
     *
     * @return InetSocketAddress
     */
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) context.channel().localAddress();
    }

    /**
     * Closes this connection
     */
    public void close() {
        active = false;
        context.flush().close().addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Writes a packet to this connection
     *
     * @param packet Packet to write
     */
    public void sendPacket(P packet) {
        context.writeAndFlush(packet);
    }

    /**
     * Writes a packet to this connection
     *
     * @param packet Packet to write
     * @param out    Buffer to write to
     */
    protected abstract void encode(P packet, ByteBuf out) throws Exception;

    /**
     * Reads the incoming buffer and converts to a packet object
     *
     * @param in  Incoming buffer to read
     * @param out List of parsed packets
     */
    protected abstract void decode(ByteBuf in, List<Object> out) throws Exception;

    /**
     * Handles incoming packets for this connection
     *
     * @param packet Incoming Packet
     */
    protected abstract void incomingPacket(P packet);

    /**
     * Handles outgoing packets for this connection
     *
     * @param packet Outgoing Packet
     * @return Optional - If present, packet will be sent to connection
     */
    protected abstract Optional<P> outgoingPacket(P packet);
}
