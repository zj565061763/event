# Gradle

[![](https://jitpack.io/v/zj565061763/event.svg)](https://jitpack.io/#zj565061763/event)

# Post Event
```kotlin
// post event
FEvent.post(Event())
```

# Event Flow

```kotlin
lifecycleScope.launch {
    FEvent.flow(Event::class.java).collect { event ->
        logMsg { "onEvent $event" }
    }
}
```

# Event Observer

```kotlin
private val _observer = object : FEventObserver<Event>(Event::class.java) {
    override fun onEvent(event: Event) {
        logMsg { "onEvent $event" }
    }
}

// register observer
_observer.register()

// unregister observer
_observer.unregister()
```

