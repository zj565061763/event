package com.sd.demo.event

import com.sd.lib.event.FEvent
import com.sd.lib.event.fEvent
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
class EventTest {
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()

   @Test
   fun `test event`() = runTest {
      val count = AtomicInteger()

      val job1 = launch {
         fEvent<TestEvent> {
            count.incrementAndGet()
         }
      }

      val job2 = launch {
         fEvent<TestEvent> {
            count.incrementAndGet()
         }
      }

      // job1 and job2 started
      runCurrent()

      FEvent.post(TestEvent())
      runCurrent()
      assertEquals(2, count.get())

      job1.cancelAndJoin()
      FEvent.post(TestEvent())
      runCurrent()
      assertEquals(3, count.get())

      job2.cancel()
   }
}

private class TestEvent