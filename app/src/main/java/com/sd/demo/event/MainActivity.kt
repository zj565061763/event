package com.sd.demo.event

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.event.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
   private val _binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(_binding.root)
      _binding.btnSample.setOnClickListener {
         startActivity(Intent(this, Sample::class.java))
      }
   }
}

inline fun logMsg(block: () -> String) {
   Log.i("event-demo", block())
}