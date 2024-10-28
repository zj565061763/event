package com.sd.demo.event

import com.sd.lib.event.FKeyedEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
class KeyedEventTest {
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()

   @Test
   fun `test event`() = runTest {
      val keyedEvent = FKeyedEvent<TestKeyedEvent>()
      val count = AtomicInteger()

      val job1 = launch {
         keyedEvent.collect("") {
            count.incrementAndGet()
         }
      }

      val job2 = launch {
         keyedEvent.collect("") {
            count.incrementAndGet()
         }
      }

      runCurrent()

      keyedEvent.emit("", TestKeyedEvent())
      runCurrent()
      assertEquals(2, count.get())

      job1.cancelAndJoin()
      keyedEvent.emit("", TestKeyedEvent())
      runCurrent()
      assertEquals(3, count.get())

      job2.cancel()
   }
}

private class TestKeyedEvent