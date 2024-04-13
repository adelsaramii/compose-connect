package com.chirrio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContent {
//            NavigatedApp()
//        }

        val serviceIntent = Intent(this, CallService::class.java)
        startService(serviceIntent)

        serviceIntent
    }
}