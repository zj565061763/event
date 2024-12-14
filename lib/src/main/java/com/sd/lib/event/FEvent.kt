package com.sd.lib.event

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainCoroutineDispatcher
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

  suspend inline fun <reified T> collect(noinline block: suspend (T) -> Unit) = collect(T::class.java, block)

  suspend fun <T> collect(
    key: Class<T>,
    block: suspend (T) -> Unit,
  ) {
    // 因为大部分收集发生在主线程，所以优先使用Dispatchers.Main.immediate可以减少一次调度
    withContext(Dispatchers.preferMainImmediate) {
      @Suppress("UNCHECKED_CAST")
      val flow = _map.getOrPut(key) { MutableSharedFlow<T>() } as MutableSharedFlow<T>
      try {
        flow.collect { block(it) }
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

inline fun <reified T> FEvent.flowOf(): Flow<T> = flowOf(T::class.java)

fun <T> FEvent.flowOf(key: Class<T>): Flow<T> = channelFlow {
  collect(key) { send(it) }
}

// 后面可能会迁移到KMP，所以使用runCatching，因为有的平台没有Dispatchers.Main.immediate
private val Dispatchers.preferMainImmediate: MainCoroutineDispatcher
  get() = runCatching { Main.immediate }.getOrElse { Main }