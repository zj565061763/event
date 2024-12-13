package com.sd.demo.event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sd.demo.event.databinding.SampleJavaBinding
import com.sd.lib.event.FEvent
import com.sd.lib.event.collect
import com.sd.lib.event.flowOf
import kotlinx.coroutines.launch

class Sample : AppCompatActivity() {
  private val _binding by lazy { SampleJavaBinding.inflate(layoutInflater) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(_binding.root)
    _binding.btnPost.setOnClickListener {
      FEvent.post(SampleEvent())
    }

    lifecycleScope.launch {
      FEvent.collect<SampleEvent> { event ->
        logMsg { "event $event" }
      }
    }

    lifecycleScope.launch {
      FEvent.flowOf<SampleEvent>().collect { event ->
        logMsg { "flowOf event $event" }
      }
    }
  }
}

private data class SampleEvent(
  val name: String = "Tome",
)