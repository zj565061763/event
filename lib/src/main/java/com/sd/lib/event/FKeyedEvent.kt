package com.sd.lib.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FKeyedEvent<T> {
   private val _flows: MutableMap<String, FlowHolder<T>> = mutableMapOf()
   private val _dispatcher = runCatching { Dispatchers.Main.immediate }.getOrDefault(Dispatchers.Main)
   private val _scope = CoroutineScope(SupervisorJob() + _dispatcher)

   fun emit(key: String, event: T) {
      _scope.launch {
         val holder = _flows.getOrPut(key) { FlowHolder(releaseAble = false) }
         holder.releaseAble = false
         holder.flow.emit(event)
      }
   }

   fun release(key: String) {
      _scope.launch {
         _flows[key]?.let { holder ->
            holder.releaseAble = true
            holder.release(key)
         }
      }
   }

   suspend fun collect(
      key: String,
      block: suspend (T) -> Unit,
   ) {
      withContext(_dispatcher) {
         val holder = _flows.getOrPut(key) { FlowHolder(releaseAble = true) }
         try {
            holder.flow.collect {
               block(it)
            }
         } finally {
            holder.release(key)
         }
      }
   }


   private inner class FlowHolder<T>(
      var releaseAble: Boolean,
   ) {
      val flow: MutableSharedFlow<T> = MutableSharedFlow()

      fun release(key: String) {
         if (releaseAble && flow.subscriptionCount.value == 0) {
            _flows.remove(key)
         }
      }
   }
}