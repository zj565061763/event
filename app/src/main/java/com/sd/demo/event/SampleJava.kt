package com.sd.demo.event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.event.databinding.SampleJavaBinding
import com.sd.lib.event.FEvent
import com.sd.lib.event.FEventObserver

class SampleJava : AppCompatActivity() {
    private val _binding by lazy { SampleJavaBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        _binding.btnPost.setOnClickListener {
            // 发送事件
            FEvent.post(JavaEvent())
        }

        // 注册监听
        _observer.register()
    }

    private val _observer = object : FEventObserver<JavaEvent>(JavaEvent::class.java) {
        override fun onEvent(event: JavaEvent) {
            logMsg { "onEvent $event" }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消注册监听
        _observer.unregister()
    }
}

private class JavaEvent