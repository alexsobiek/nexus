package com.alexsobiek.nexus.netty;

import com.alexsobiek.nexus.NexusLibrary;
import com.alexsobiek.nexus.netty.channel.Pipeline;

import java.net.InetSocketAddress;

public abstract class AbstractBuilder<T extends AbstractSocket<?, ?>, B extends AbstractBuilder<T, B>> extends NexusLibrary.Builder<T> {
    protected InetSocketAddress address;
    protected int threads = 4;
    protected Pipeline<?> pipeline;

    /**
     * Sets the address
     *
     * @param address Address to use
     * @return Builder
     */
    @SuppressWarnings("unchecked")
    public B address(InetSocketAddress address) {
        this.address = address;
        return (B) this;
    }

    /**
     * Sets the number of threads to use
     *
     * @param threads Number of threads to use
     * @return Builder
     */
    @SuppressWarnings("unchecked")
    public B threads(int threads) {
        this.threads = threads;
        return (B) this;
    }

    /**
     * Sets the {@link Pipeline} object to use
     *
     * @param pipeline Pipeline object
     * @return Builder
     */
    @SuppressWarnings("unchecked")
    public B pipeline(Pipeline<?> pipeline) {
        this.pipeline = pipeline;
        return (B) this;
    }

    protected InetSocketAddress address() {
        return address != null ? address : new InetSocketAddress(3000); // use default port 3000 if not set
    }

    protected Pipeline<?> pipeline() {
        if (pipeline == null) throw new RuntimeException("Pipeline cannot be empty");
        else return pipeline;
    }

    public NexusLibrary.BuildableLibrary<T> build() {
        return new NexusLibrary.BuildableLibrary<T>() {
            @Override
            protected T build() {
                return AbstractBuilder.this.doBuild();
            }
        };
    }

    protected abstract T doBuild();
}
