package com.sd.lib.event

import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

abstract class FEventObserver<T>(
   private val clazz: Class<T>,
) {
   private val _scope = MainScope()
   private var _registerJob: Job? = null

   @Synchronized
   fun register() {
      if (_registerJob != null) return
      _registerJob = _scope.launch {
         FEvent.flow(clazz).collect {
            onEvent(it)
         }
      }
   }

   @Synchronized
   fun unregister() {
      _registerJob?.cancel()
      _registerJob = null
   }

   abstract fun onEvent(event: T)
}