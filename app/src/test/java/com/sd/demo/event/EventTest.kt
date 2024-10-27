package com.sd.demo.event

import com.sd.lib.event.FEvent
import com.sd.lib.event.fEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
class EventTest {
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()

   @Test
   fun `test event`() = runTest {
      val count = AtomicInteger()

      val job = launch {
         fEvent<TestEvent> {
            count.incrementAndGet()
         }
      }

      launch {
         fEvent<TestEvent> {
            count.incrementAndGet()
         }
      }

      advanceUntilIdle()

      FEvent.post(TestEvent())
      advanceUntilIdle()
      assertEquals(2, count.get())

      job.cancelAndJoin()
      FEvent.post(TestEvent())
      advanceUntilIdle()
      assertEquals(3, count.get())
   }
}

private class TestEvent