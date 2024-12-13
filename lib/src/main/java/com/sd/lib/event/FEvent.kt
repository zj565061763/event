package com.sd.lib.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object FEvent {
  private val _flows = mutableMapOf<Class<*>, MutableSharedFlow<*>>()
  private val _scope = MainScope()

  @JvmStatic
  fun post(event: Any) {
    _scope.launch {
      emit(event)
    }
  }

  suspend fun emit(event: Any) {
    withContext(Dispatchers.Main) {
      @Suppress("UNCHECKED_CAST")
      val flow = _flows[event.javaClass] as? MutableSharedFlow<Any>
      flow?.emit(event)
    }
  }

  suspend fun <T> collect(clazz: Class<T>, block: suspend (T) -> Unit) {
    withContext(Dispatchers.Main) {
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

inline fun <reified T> FEvent.flowOf(): Flow<T> = flowOf(T::class.java)

fun <T> FEvent.flowOf(clazz: Class<T>): Flow<T> {
  return channelFlow {
    collect(clazz) {
      send(it)
    }
  }
}