package com.sd.demo.event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sd.demo.event.databinding.SampleJavaBinding
import com.sd.lib.event.FEvent
import kotlinx.coroutines.launch

class Sample : AppCompatActivity() {
   private val _binding by lazy { SampleJavaBinding.inflate(layoutInflater) }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(_binding.root)
      _binding.btnPost.setOnClickListener {
         FEvent.post(KotlinEvent())
      }

      lifecycleScope.launch {
         FEvent.flow(KotlinEvent::class.java).collect { event ->
            logMsg { "onEvent $event" }
         }
      }
   }
}

private data class KotlinEvent(
   val name: String = "Tome",
)