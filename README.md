# Gradle

[![](https://jitpack.io/v/zj565061763/event.svg)](https://jitpack.io/#zj565061763/event)

# Sample

```kotlin
// Post event
FEvent.post(SampleEvent())
```

```kotlin
lifecycleScope.launch {
   FEvent.flowOf<SampleEvent>().collect { event ->
      // Handle event
   }
}
```