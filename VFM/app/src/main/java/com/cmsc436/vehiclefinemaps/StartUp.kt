package com.cmsc436.vehiclefinemaps

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class StartUp : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.startup)
        Log.i("here", "start oncreate")

        val mHandler = Handler()
        mHandler.postDelayed({finish()}, 5000L)

    }

}