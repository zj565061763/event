# Gradle

[![](https://jitpack.io/v/zj565061763/event.svg)](https://jitpack.io/#zj565061763/event)

# Sample

```kotlin
// Post event
FEvent.post(SampleEvent())
```

```kotlin
lifecycleScope.launch {
  FEvent.collect<SampleEvent> { event ->
    logMsg { "event $event" }
  }
}

lifecycleScope.launch {
  FEvent.flowOf<SampleEvent>().collect { event ->
    // Collect event
  }
}
```