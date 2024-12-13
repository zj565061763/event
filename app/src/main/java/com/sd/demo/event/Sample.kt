package com.sd.demo.event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sd.demo.event.databinding.SampleJavaBinding
import com.sd.lib.event.FEvent
import com.sd.lib.event.flowOf
import com.sd.lib.event.post
import kotlinx.coroutines.launch

class Sample : AppCompatActivity() {
  private val _binding by lazy { SampleJavaBinding.inflate(layoutInflater) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(_binding.root)
    _binding.btnPost.setOnClickListener {
      FEvent.post(SampleEvent())
      FEvent.post(ParentEvent.ChildEvent(), ParentEvent::class.java)
    }
    collectSampleEvent()
    collectSealedEvent()
  }

  private fun collectSampleEvent() {
    lifecycleScope.launch {
      FEvent.flowOf<SampleEvent>().collect { event ->
        logMsg { "flowOf SampleEvent -> $event" }
      }
    }
  }

  private fun collectSealedEvent() {
    lifecycleScope.launch {
      FEvent.flowOf<ParentEvent>().collect { event ->
        logMsg { "flowOf ParentEvent -> $event" }
      }
    }
    lifecycleScope.launch {
      FEvent.flowOf<ParentEvent.ChildEvent>().collect { event ->
        logMsg { "flowOf ChildEvent -> $event" }
      }
    }
  }
}

private data class SampleEvent(
  val name: String = "Tome",
)

sealed interface ParentEvent {
  data class ChildEvent(val name: String = "child") : ParentEvent
}