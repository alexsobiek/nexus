package com.alexsobiek.nexus;

import com.alexsobiek.nexus.thread.NexusThreadFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ForkJoinPool;

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

    protected NexusThreadFactory getThreadFactory() {
        return nexus.threadFactory;
    }

    protected ForkJoinPool getExecutor() {
        return nexus.forkJoinPool;
    }

    @RequiredArgsConstructor
    public abstract static class BuildableLibrary<T extends NexusLibrary> extends NexusLibrary {

        protected T doBuild(Nexus nexus) {
            init(nexus);
            T lib = build();
            lib.init(nexus);
            return lib;
        }

        protected abstract T build();
    }

    public static abstract class Builder<T extends NexusLibrary> {

        /**
         * Creates a buildable library to be built by Nexus
         *
         * @return BuildableLibrary
         */

        public abstract BuildableLibrary<T> build();
    }
}
