package com.sd.lib.event

import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

object FEvent {
   private val _scope = MainScope()
   var isDebug = false

   private val _flows: MutableMap<Class<*>, WeakRef<MutableSharedFlow<*>>> = mutableMapOf()
   private val _refQueue = ReferenceQueue<Any>()

   /**
    * 发送事件
    */
   @JvmStatic
   fun post(event: Any) {
      synchronized(this@FEvent) {
         @Suppress("UNCHECKED_CAST")
         val flow = _flows[event.javaClass]?.get() as? MutableSharedFlow<Any> ?: return
         logMsg { "post -----> $event (${flow.subscriptionCount.value})" }
         _scope.launch { flow.emit(event) }
      }
   }

   /**
    * 返回事件[clazz]对应的[Flow]
    */
   fun <T> flow(clazz: Class<T>): Flow<T> {
      return synchronized(this@FEvent) {
         releaseRef()
         @Suppress("UNCHECKED_CAST")
         (_flows[clazz]?.get() as? MutableSharedFlow<T>) ?: MutableSharedFlow<T>().also { instance ->
            _flows[clazz] = WeakRef(
               referent = instance,
               queue = _refQueue,
               clazz = clazz,
            )
            logMsg { "+++++ ${clazz.name} size:${_flows.size}" }
         }
      }.asSharedFlow()
   }

   private fun releaseRef() {
      while (true) {
         val ref = _refQueue.poll() ?: return
         check(ref is WeakRef)
         _flows.remove(ref.clazz)
         logMsg { "----- ${ref.clazz.name} size:${_flows.size}" }
      }
   }

   private class WeakRef<T>(
      referent: T,
      queue: ReferenceQueue<in T>,
      val clazz: Class<*>,
   ) : WeakReference<T>(referent, queue)
}

private inline fun logMsg(block: () -> String) {
   if (FEvent.isDebug) {
      Log.i("FEvent", block())
   }
}