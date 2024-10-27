package com.sd.demo.event

import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class EventTest {
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()

   @Test
   fun `test event`() = runTest {

   }
}