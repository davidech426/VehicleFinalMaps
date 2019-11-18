package com.cmsc436.vehiclefinemaps

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.jar.Manifest
import kotlin.random.Random



class LetsDrive : AppCompatActivity(){
    private val PERMISSION_ID = 101
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var startBtn: Button
    lateinit var endBtn: Button
    private var buttonCount = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lets_drive)
        startBtn = findViewById(R.id.start_btn)
        endBtn = findViewById(R.id.end_button)

        findViewById<Button>(R.id.end_button).visibility = View.GONE
        findViewById<Button>(R.id.start_btn).visibility = View.VISIBLE

        buttonCount = 0

        val text = findViewById<TextView>(R.id.percent)
        text.setText(Random.nextInt(100).toString() + "%")


        if(checkPermissions() == false) {
            requestPermissions()
        }


        val locationMngr = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.lastLocation


        startBtn.setOnClickListener{
            buttonCount++;

            if (buttonCount % 2 == 0) {
                findViewById<Button>(R.id.end_button).visibility = View.GONE
                findViewById<Button>(R.id.start_btn).visibility = View.VISIBLE
            } else {
                findViewById<Button>(R.id.end_button).visibility = View.VISIBLE
                findViewById<Button>(R.id.start_btn).visibility = View.GONE
            }

        }

        endBtn.setOnClickListener{
            buttonCount++;

            if (buttonCount % 2 == 0) {
                findViewById<Button>(R.id.end_button).visibility = View.GONE
                findViewById<Button>(R.id.start_btn).visibility = View.VISIBLE
            } else {
                findViewById<Button>(R.id.end_button).visibility = View.VISIBLE
                findViewById<Button>(R.id.start_btn).visibility = View.GONE
            }

            //SAVE CURRENT DRIVE INFORMATION


        }





    }



    private fun checkPermissions(): Boolean {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ) {
            // Permission is not granted
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID)
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


}