package com.sd.demo.event

import com.sd.lib.event.FEvent
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
         FEvent.flowOf<TestEvent>().collect {
            count.incrementAndGet()
         }
      }

      val job2 = launch {
         FEvent.flowOf<TestEvent>().collect {
            count.incrementAndGet()
         }
      }

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