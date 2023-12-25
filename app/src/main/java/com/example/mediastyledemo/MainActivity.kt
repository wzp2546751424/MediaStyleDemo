package com.example.mediastyledemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.button).setOnClickListener {
            startService1()
        }
        findViewById<View>(R.id.button1).setOnClickListener {
            stopService()
        }
    }

    private fun startService1() {
        val intent = Intent(this, FakeMusicService::class.java)
        startForegroundService(intent)
    }

    private fun stopService() {
        FakeMusicService.stopThisService()
    }

}