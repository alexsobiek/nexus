# Nexus
Nexus is a collection of libraries of common implementations I've had to write. It's currently in its infancy and not
available on any maven repositories yet, but the goal is to eventually get to the point of a release.

## Core
The core library has the most essential parts of Nexus itself, including the ability to build and inject itself into
all other libraries. It features general purpose async & multithreading utilities, event bus, scheduler, and utility
package with various helpful classes.

### Getting Started with Core
```java
import com.alexsobiek.nexus.Nexus;
import com.alexsobiek.nexus.event.Event;

class MyApp {
    Nexus nexus;
    public MyApp() {
        nexus = Nexus.builder().build(); // Build nexus with default settings
        
        // Schedule repeating task every 1 second with 0 delay
        nexus.scheduler().scheduleAtFixedRate(this::myRepeatingTask, 0L, 1000L);
        
        // Listen for MyEvent
        nexus.eventBus().listen(MyEvent.class, this::myEventListener);
        
        // Post MyEvent
        nexus.eventBus().post(new MyEvent());
    }
    
    void myRepeatingTask() {
        System.out.println("Task called");
    }
    
    void myEventListener(MyEvent event) {
        // do something with event
    }
}

// Simple event class
class MyEvent implements Event {
}
```
#### Async Events
`MyEvent` in the above sample code extends `Event`, which will be posted to all event listeners synchronously in order
of listener priority. If listener priority or order does not matter and the event itself is immutable, it can implement
`AsyncEvent` instead, and all event listeners will be called asynchronously.

### Core Utilities
#### Lazy
Lazy initialization allows you to delay the initialization of an object until it is needed, which can improve the
performance of object creation and memory usage (if the lazy supplier is never called).

```java
Lazy<MyApp> myApp = new Lazy(MyApp::new);
```
This provides the instructions on how to create a new `MyApp` instance, but does not create one until `myApp.get()` is
called. After that, the instance is stored within the Lazy object and each call after will return the same instance.

## Dependency Injection
The inject package provides a very basic library for constructing and injecting dependencies into objects.
```java
import com.alexsobiek.nexus.Nexus;
import com.alexsobiek.nexus.event.EventBus;
import com.alexsobiek.nexus.inject.NexusInject;
import com.alexsobiek.nexus.inject.annotation.Inject;
import com.alexsobiek.nexus.inject.dependency.DependencyProvider;

class MyApp {
    public MyApp() {
        Nexus nexus = Nexus.builder().build();
        NexusInject inject = nexus.library(NexusInject.buildable());

        // Construct MyLib, injecting a String and the Nexus EventBus
        inject.construct(MyLibrary.class, new MyDepProvider(nexus)).thenAccept(opt -> {
            if (opt.isPresent()) {
                MyLibrary lib = opt.get();
                
                // do something with lib
            }
        });
    }
}

class MyDepProvider extends DependencyProvider {
    private final Nexus nexus;
    
    public MyDepProvider(Nexus nexus) {
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

class MyLibrary {
    @Inject(identifier = "testString")
    public String testString;

    @Inject
    public EventBus eventBus;
}
```

