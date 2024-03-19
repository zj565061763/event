package com.sd.demo.event

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.event.databinding.ActivityMainBinding
import com.sd.lib.event.FEvent

class MainActivity : AppCompatActivity() {
    private val _binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        _binding.btnSampleJava.setOnClickListener {
            startActivity(Intent(this, SampleJava::class.java))
        }
        _binding.btnSampleKotlin.setOnClickListener {
            startActivity(Intent(this, SampleKotlin::class.java))
        }
    }

    companion object {
        init {
            FEvent.isDebug = true
        }
    }
}

inline fun logMsg(block: () -> String) {
    Log.i("event-demo", block())
}