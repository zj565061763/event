package com.sd.lib.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object FEvent {
   private val _dispatcher = runCatching { Dispatchers.Main.immediate }.getOrDefault(Dispatchers.Main)
   private val _scope = CoroutineScope(SupervisorJob() + _dispatcher)
   private val _flows: MutableMap<Class<*>, MutableSharedFlow<*>> = mutableMapOf()

   @JvmStatic
   fun post(event: Any) {
      _scope.launch {
         @Suppress("UNCHECKED_CAST")
         val flow = _flows[event.javaClass] as? MutableSharedFlow<Any>
         flow?.emit(event)
      }
   }

   suspend fun <T> collect(
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
            _flows.remove(clazz)
         }
      }
   }
}

suspend inline fun <reified T> fEvent(noinline block: suspend (T) -> Unit) {
   FEvent.collect(
      clazz = T::class.java,
      block = block,
   )
}