package com.sd.demo.event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sd.demo.event.databinding.SampleJavaBinding
import com.sd.lib.event.FEvent
import com.sd.lib.event.collect
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

  private fun collectSealedEvent() {
    lifecycleScope.launch {
      FEvent.collect<ParentEvent> { event ->
        logMsg { "ParentEvent $event" }
      }
    }
    lifecycleScope.launch {
      FEvent.collect<ParentEvent.ChildEvent> { event ->
        logMsg { "ChildEvent $event" }
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