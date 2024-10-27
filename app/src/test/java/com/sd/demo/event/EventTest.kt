package com.sd.demo.event

import app.cash.turbine.test
import com.sd.lib.event.FEvent
import com.sd.lib.event.fEvent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EventTest {
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()

   @Test
   fun `test flow`() = runTest {
      val flow1 = fEvent<Any>()
      val flow2 = fEvent<Any>()
      val flow3 = fEvent<Any>()
      assertEquals(false, flow1 == flow2)
      assertEquals(false, flow2 == flow3)
   }

   @Test
   fun `test event`() = runTest {
      fEvent<TestEvent>().test {
         FEvent.post(TestEvent())
         FEvent.post(TestEvent())
         awaitItem()
         awaitItem()
      }
   }
}

private class TestEvent