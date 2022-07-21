package com.alexsobiek.nexus.event;

/**
 * An event that may be posted asynchronously.
 *
 * Listeners for this event will NOT be called in any prioritized order.
 * Furthermore, the event bus will parallelize calls meaning it will not
 * wait for a listener to finish before calling the next.
 */
public interface AsyncEvent {
}
