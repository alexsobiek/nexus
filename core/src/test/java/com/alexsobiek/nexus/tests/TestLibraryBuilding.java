package com.alexsobiek.nexus.tests;

import com.alexsobiek.nexus.Nexus;
import com.alexsobiek.nexus.NexusLibrary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Test library building")
public class TestLibraryBuilding {


    @Test
    public void testBuilding() {
        Nexus nexus = Nexus.builder().build();
        TestLib testLib = nexus.library(TestLib.builder().threads(4).build());

        assertNotNull(nexus.eventBus()); // Test Nexus built correctly for good measure
        assertEquals(4, testLib.getThreadCount()); // Test the thread count we set is used when building
    }


    static class TestLib extends NexusLibrary {
        private final int threadCount;

        TestLib(int threadCount) {
            this.threadCount = threadCount;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public static TestLibBuilder builder() {
            return new TestLibBuilder();
        }
    }

    static class TestLibBuilder extends NexusLibrary.Builder<TestLib> {
        private int threads = -1;

        public TestLibBuilder threads(int threads) {
            this.threads = threads;
            return this;
        }

        @Override
        public NexusLibrary.BuildableLibrary<TestLib> build() {
            return new NexusLibrary.BuildableLibrary<TestLib>() {
                @Override
                protected TestLib build() {
                    return new TestLib(threads);
                }
            };
        }
    }
}
