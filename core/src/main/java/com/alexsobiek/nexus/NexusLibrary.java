package com.alexsobiek.nexus;

import com.alexsobiek.nexus.thread.NexusThreadFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import java.util.concurrent.*;
import java.util.function.Consumer;

@Getter
public abstract class NexusLibrary {
    private Nexus nexus;

    /**
     * Internal init method, used when constructing libraries
     *
     * @param nexus Nexus instance
     */
    protected void init(Nexus nexus) {
        this.nexus = nexus;
    }

    protected Logger getLogger() {
        return nexus.logger;
    }

    protected NexusThreadFactory getThreadFactory() {
        return nexus.threadFactory;
    }

    protected ForkJoinPool getExecutor() {
        return nexus.forkJoinPool;
    }

    @RequiredArgsConstructor
    public abstract static class BuildableLibrary<T extends NexusLibrary> extends NexusLibrary {
        private final Consumer<T> consumer;

        protected void doBuild(Nexus nexus) {
            this.init(nexus);
            consumer.accept(this.build());
        }

        protected abstract T build();
    }

    public static abstract class Builder<T extends NexusLibrary>  {

        /**
         * Creates a buildable library. This essentially promises Nexus will build the library, and it will be consumed
         * once built.
         *
         * @param then Consumer of built library
         * @return BuildableLibrary
         */
        public abstract BuildableLibrary<T> onBuild(Consumer<T> then);
    }
}
