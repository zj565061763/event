package com.sd.demo.event

import app.cash.turbine.test
import com.sd.lib.event.FEvent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EventTest {
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()

   @Test
   fun `test flow`() = runTest {
      val flow1 = FEvent.flow(Any::class.java)
      val flow2 = FEvent.flow(Any::class.java)
      val flow3 = FEvent.flow(Any::class.java)
      assertEquals(false, flow1 == flow2)
      assertEquals(false, flow2 == flow3)
   }

   @Test
   fun `test event`() = runTest {
      FEvent.flow(TestEvent::class.java).test {
         FEvent.post(TestEvent())
         FEvent.post(TestEvent())
         awaitItem()
         awaitItem()
      }
   }
}

private class TestEvent