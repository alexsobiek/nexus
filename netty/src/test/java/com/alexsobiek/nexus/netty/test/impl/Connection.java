package com.alexsobiek.nexus.netty.test.impl;

import com.alexsobiek.nexus.netty.proto.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Optional;

public class Connection extends com.alexsobiek.nexus.netty.Connection<ByteBuf, Packet<ByteBuf>> {
    private final ChannelHandler.Type type;
    public Connection(ChannelHandlerContext context, ChannelHandler.Type type) {
        super(context);
        this.type = type;
    }

    @Override
    protected void encode(Packet<ByteBuf> packet, ByteBuf out) throws Exception {

    }

    @Override
    protected void decode(ByteBuf in, List<Object> out) throws Exception {

    }

    @Override
    protected void incomingPacket(Packet<ByteBuf> packet) {

    }

    @Override
    protected Optional<Packet<ByteBuf>> outgoingPacket(Packet<ByteBuf> packet) {
        return Optional.empty();
    }

    public ChannelHandler.Type getType() {
        return type;
    }
}
