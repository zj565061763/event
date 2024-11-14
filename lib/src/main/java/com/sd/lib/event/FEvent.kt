package com.sd.lib.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object FEvent {
   private val _flows: MutableMap<Class<*>, MutableSharedFlow<*>> = mutableMapOf()
   private val _dispatcher = runCatching { Dispatchers.Main.immediate }.getOrDefault(Dispatchers.Main)
   private val _scope = CoroutineScope(SupervisorJob() + _dispatcher)

   @JvmStatic
   fun post(event: Any) {
      _scope.launch {
         @Suppress("UNCHECKED_CAST")
         val flow = _flows[event.javaClass] as? MutableSharedFlow<Any>
         flow?.emit(event)
      }
   }

   inline fun <reified T> flowOf(): Flow<T> {
      return flowOf(T::class.java)
   }

   fun <T> flowOf(clazz: Class<T>): Flow<T> {
      return channelFlow {
         collect(clazz) {
            send(it)
         }
      }
   }

   private suspend fun <T> collect(
      clazz: Class<T>,
      block: suspend (T) -> Unit,
   ) {
      withContext(_dispatcher) {
         @Suppress("UNCHECKED_CAST")
         val flow = _flows.getOrPut(clazz) { MutableSharedFlow<Any>() } as MutableSharedFlow<T>
         try {
            flow.collect {
               block(it)
            }
         } finally {
            if (flow.subscriptionCount.value == 0) {
               _flows.remove(clazz)
            }
         }
      }
   }
}