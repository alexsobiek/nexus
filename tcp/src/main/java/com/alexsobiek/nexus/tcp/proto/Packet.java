package com.alexsobiek.nexus.tcp.proto;

import io.netty.buffer.ByteBuf;

/**
 * Abstract packet class
 *
 * @param <B> Buffer type
 */
public abstract class Packet<B extends ByteBuf> {
    public Packet(B in) {
    }

    public abstract void write(B out);
}
