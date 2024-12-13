package com.sd.lib.event

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object FEvent {
  private val _map = mutableMapOf<Class<*>, MutableSharedFlow<*>>()

  suspend fun <T : Any> emit(
    event: T,
    key: Class<T> = event.javaClass,
  ) {
    withContext(Dispatchers.Main) {
      @Suppress("UNCHECKED_CAST")
      val flow = _map[key] as? MutableSharedFlow<T>
      flow?.emit(event)
    }
  }

  suspend fun <T> collect(
    key: Class<T>,
    block: suspend (T) -> Unit,
  ) {
    withContext(Dispatchers.Main) {
      @Suppress("UNCHECKED_CAST")
      val flow = _map.getOrPut(key) { MutableSharedFlow<T>() } as MutableSharedFlow<T>
      try {
        flow.collect {
          block(it)
        }
      } finally {
        if (flow.subscriptionCount.value == 0) {
          _map.remove(key)
        }
      }
    }
  }
}

@JvmOverloads
fun <T : Any> FEvent.post(
  event: T,
  key: Class<T> = event.javaClass,
) {
  @OptIn(DelicateCoroutinesApi::class)
  GlobalScope.launch(Dispatchers.Main) {
    emit(event, key)
  }
}

suspend inline fun <reified T> FEvent.collect(noinline block: suspend (T) -> Unit) = collect(T::class.java, block)

inline fun <reified T> FEvent.flowOf(): Flow<T> = flowOf(T::class.java)
fun <T> FEvent.flowOf(key: Class<T>): Flow<T> = channelFlow { collect(key) { send(it) } }