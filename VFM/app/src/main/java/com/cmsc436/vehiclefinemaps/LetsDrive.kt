package com.cmsc436.vehiclefinemaps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import com.google.android.gms.location.*
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
import java.net.URL


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


    val mainHandler = Handler(Looper.getMainLooper())
    val handler = Handler()
    private lateinit var mRunnable:Runnable

    val app_id = "iN2zzYvXRAmxbvmz5vMw"
    val app_code = "Djl7Hs_LIhwN2tmPfQGiNA"

    var lat1: Double? = null
    var lat2: Double? = null

    var lng1: Double? = null
    var lng2: Double? = null

    var limit = 0.0f
    var speed = 56.0f

    var avSpeed = 0
    var avRisk = 0.0f
    var counter = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lets_drive)
        startBtn = findViewById(R.id.start_btn)
        endBtn = findViewById(R.id.end_button)

        findViewById<Button>(R.id.end_button).visibility = View.GONE
        findViewById<Button>(R.id.start_btn).visibility = View.VISIBLE

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Previous")
        mAuth = FirebaseAuth.getInstance()

        buttonCount = 0

        val text = findViewById<TextView>(R.id.percent)
        text.setText("0%")



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
                mainHandler.post(object : Runnable {
                    override fun run() {
                        getLastLocation()
                        mainHandler.postDelayed(this, 5000)
                    }
                })
                handler.post(object : Runnable {
                    override fun run() {
                        getSpeedLimit()
                        mainHandler.postDelayed(this, 30000)
                    }
                })
            }
            //set the start time of the duration
            startTime=System.currentTimeMillis()

        }

        endBtn.setOnClickListener{
            buttonCount++;

            if (buttonCount % 2 == 0) {
                findViewById<Button>(R.id.end_button).visibility = View.GONE
                findViewById<Button>(R.id.start_btn).visibility = View.VISIBLE
                mainHandler.removeCallbacksAndMessages(null)
                handler.removeCallbacksAndMessages(null)
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
            val time= SimpleDateFormat("hh:mm:ss a")
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
            currentUser.child(driveId).child("speed").setValue(avSpeed)
            currentUser.child(driveId).child("likelihood").setValue(avRisk)


        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        requestNewLocationData()
                        Log.d("LOCATION", "Latitude: " + location.latitude.toString())
                        Log.d("LOCATION", "Longitude: " + location.longitude.toString())

                        if (lat1 == null) {
                            lat1 = location.latitude
                            lng1 = location.longitude
                        } else {
                            lat2 = location.latitude
                            lng2 = location.longitude
                        }

                        val text = findViewById<TextView>(R.id.percent)

                        //speed is not set within emulator
                        //val speed = location.speed as Float
                        var risk = 0.0f
                        if (speed > limit) {
                            val diff = speed - limit
                            if (limit < 55) {
                                if (diff <= 16) {
                                    risk = ((diff / 32) * 100)
                                } else {
                                    risk = ((diff / 55) + .5f) * 100f
                                    if (risk > 100f) {
                                        risk = 100f
                                    }
                                }
                            }
                            else {
                                if (diff <= 11) {
                                    risk = (diff / 22) * 100f
                                } else {
                                    risk = ((diff / 40f) + .5f) * 100f
                                    if (risk > 100f) {
                                        risk = 100f
                                    }
                                }
                            }
                        }
                        text.setText(risk.toString() + "%")
                        counter += 1
                        avRisk += risk
                        avSpeed += speed as Int
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            Log.d("LOCATION", mLastLocation.latitude.toString())
            Log.d("LOCATION", mLastLocation.longitude.toString())
            if (lat1 == null) {
                lat1 = mLastLocation.latitude
                lng1 = mLastLocation.longitude
            } else {
                if (lat2 == null) {
                    lat2 = mLastLocation.latitude
                    lng2 = mLastLocation.longitude
                } else {
                    lat1 = lat2
                    lng1 = lng2
                    lat2 = mLastLocation.latitude
                    lng2 = mLastLocation.longitude
                }
            }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private fun getSpeedLimit() {
        if (lat1 != null && lat2 != null) {
            val request =
                "https://route.cit.api.here.com/routing/7.2/calculateroute.json?jsonAttributes=1&waypoint0=$lat1,$lng1&waypoint1=$lat2,$lng2&departure=2019-01-18T10:33:00&routeattributes=sh,lg&legattributes=li&linkattributes=nl,fc&mode=fastest;car;traffic:enabled&app_code=$app_code&app_id=$app_id"
            Log.d("response", request)
            val ret = URL(request).readText()
            Log.d("response", ret)
            var retParsed = ret.substringAfter(""""speedLimit":""")
            if (retParsed != null && retParsed != ret) {
                retParsed = retParsed.substringBefore(".")
            }
            if (retParsed != null && retParsed != ret) {
                limit = retParsed.toFloat()
                limit *= 2.237f //converting from meters per second to miles per hour
            }

        }
        lat1 = lat2
        lng1 = lng2
    }

}