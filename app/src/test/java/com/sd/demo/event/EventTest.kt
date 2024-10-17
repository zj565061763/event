package com.sd.demo.event

import app.cash.turbine.test
import com.sd.lib.event.FEvent
import com.sd.lib.event.FEventObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventTest {

   @Before
   fun setUp() {
      Dispatchers.setMain(StandardTestDispatcher())
   }

   @After
   fun tearDown() {
      Dispatchers.resetMain()
   }

   @Test
   fun testFlow() = runTest {
      FEvent.flow(TestEvent::class.java).test {
         val repeat = 5
         repeat(repeat) {
            FEvent.post(TestEvent())
         }
         repeat(repeat) {
            assertEquals(TestEvent(), awaitItem())
         }
      }
   }

   @Test
   fun testObserver() = runTest {
      val repeat = 5
      var count = 0

      val observer = object : FEventObserver<TestEvent>(TestEvent::class.java) {
         override fun onEvent(event: TestEvent) {
            count++
         }
      }

      kotlin.run {
         observer.register()
         repeat(repeat) {
            FEvent.post(TestEvent())
         }
         advanceUntilIdle()
         assertEquals(repeat, count)
      }

      kotlin.run {
         observer.unregister()
         repeat(repeat) {
            FEvent.post(TestEvent())
         }
         advanceUntilIdle()
         assertEquals(repeat, count)
      }
   }
}

private data class TestEvent(
   val name: String = "Tom",
)