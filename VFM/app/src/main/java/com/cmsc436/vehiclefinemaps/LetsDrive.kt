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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest
import kotlin.random.Random

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener



class LetsDrive : AppCompatActivity(){
    private val PERMISSION_ID = 101
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var startBtn: Button
    lateinit var endBtn: Button
    private var buttonCount = 0

    //David's variables
    var startTime: Long =0
    lateinit var dateOfDrive:String
    lateinit var timeOfDrive:String

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lets_drive)
        startBtn = findViewById(R.id.start_btn)
        endBtn = findViewById(R.id.end_button)

        findViewById<Button>(R.id.end_button).visibility = View.GONE
        findViewById<Button>(R.id.start_btn).visibility = View.VISIBLE

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Previous")
        mAuth = FirebaseAuth.getInstance()

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
            //set the start time of the duration
            startTime=System.currentTimeMillis()

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
            //once drive is done add date and time of drive to list
            // and then add it to the database


            //this formats the duration, date and time calculated to string form after drive ends
            val endTime=System.currentTimeMillis()
            val duration=endTime-startTime
            val durationFormat=String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
            );
            val date= SimpleDateFormat("M/dd/yyyy")
            dateOfDrive=date.format(Date())
            val time= SimpleDateFormat("hh:mm:ss")
            timeOfDrive=time.format(Date())


            //Firebase database format for Previous Drives
            // Previous
            //  |   (the previous drives table contains all the users and each of their drives)
            //  |
            //  - UserId1
            //       |      (contains all drives from this user
            //       |
            //        - DriveId1
            //              |
            //              |   (contains date,time,duration of this particalar drive)
            // etc....

            //this creates a new previos drive when user hits end drive and adds it to database
            val userId=mAuth!!.currentUser!!.uid
            val currentUser=mDatabaseReference!!.child(userId)
            //should create a unique key value
            val driveId=currentUser.push().key as String

            currentUser.child(driveId).child("date").setValue(dateOfDrive)
            currentUser.child(driveId).child("time").setValue(timeOfDrive)
            currentUser.child(driveId).child("duration").setValue(durationFormat)


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