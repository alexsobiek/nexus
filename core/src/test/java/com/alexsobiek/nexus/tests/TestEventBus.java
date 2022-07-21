package com.alexsobiek.nexus.tests;

import com.alexsobiek.nexus.Nexus;
import com.alexsobiek.nexus.event.Event;
import com.alexsobiek.nexus.event.EventBus;
import com.alexsobiek.nexus.event.EventSubscriber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Test event bus")
public class TestEventBus {
    static EventBus<Event> eventBus = Nexus.builder().build().eventBus();

    @Test
    @DisplayName("Test event bus priority")
    void testPriority() {
        AtomicInteger counter = new AtomicInteger();

        EventSubscriber<TestEvent> s20 = eventBus.listen(TestEvent.class, 20, event -> {
            counter.addAndGet(10); // increment by 10;
            assertEquals(10, counter.get()); // this should run first, therefore it should be 10
        });

        EventSubscriber<TestEvent> s10 = eventBus.listen(TestEvent.class, 10, event -> {
            counter.addAndGet(10); // increment by 10;
            assertEquals(20, counter.get()); // this should run second, therefore it should 20
        });

        EventSubscriber<TestEvent> s = eventBus.listen(TestEvent.class, event -> { // Call event with no priority (always runs last)
            counter.addAndGet(10); // increment by 10;
            assertEquals(30, counter.get()); // this should run last, therefore it should 30
        });

        // Close all the subscribers
        s20.close();
        s10.close();
        s.close();

        // Make sure there's no more TestEvent subscribers
        assertTrue(eventBus.subscribers(TestEvent.class).isEmpty());
    }
}

class TestEvent implements Event {
}