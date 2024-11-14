package com.sd.demo.event

import app.cash.turbine.test
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

      FEvent.emit(TestEvent())
      runCurrent()
      assertEquals(2, count.get())

      job1.cancelAndJoin()
      FEvent.post(TestEvent())
      runCurrent()
      assertEquals(3, count.get())

      job2.cancel()
   }

   @Test
   fun `test flow`() = runTest {
      FEvent.flowOf<String>().test {
         FEvent.emit("1")
         FEvent.emit("1")
         FEvent.emit("1")
         assertEquals("1", awaitItem())
         assertEquals("1", awaitItem())
         assertEquals("1", awaitItem())
      }
   }
}

private class TestEvent