package com.alexsobiek.nexus.inject.tests;

import com.alexsobiek.nexus.Nexus;
import com.alexsobiek.nexus.event.EventBus;
import com.alexsobiek.nexus.inject.NexusInject;
import com.alexsobiek.nexus.inject.annotation.Inject;
import com.alexsobiek.nexus.inject.dependency.DependencyProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Tests dependency injection")
public class TestDepInjection {


    @Test
    public void testDepInjection() {
        Nexus nexus = Nexus.builder().build();
        NexusInject inject = nexus.library(NexusInject.buildable());

        inject.construct(TestClass.class, new TestDepProvider(nexus)).thenAccept(opt -> {
            assertTrue(opt.isPresent());
            TestClass instance = opt.get();

            assertEquals(instance.testString, "test");
            assertEquals(instance.eventBus, nexus.eventBus());
        });

    }

    static class TestDepProvider extends DependencyProvider {
        private final Nexus nexus;

        public TestDepProvider(Nexus nexus) {
            this.nexus = nexus;
            supply(String.class, "testString", this::testStringSupplier);
            supply(EventBus.class, this::eventBusSupplier);
        }

        private String testStringSupplier() {
            return "test";
        }

        private EventBus eventBusSupplier() {
            return nexus.eventBus();
        }
    }

    static class TestClass {
        @Inject(identifier = "testString")
        public String testString;

        @Inject
        public EventBus eventBus;
    }
}