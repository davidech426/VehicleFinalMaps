package com.cmsc436.vehiclefinemaps

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import java.util.concurrent.TimeUnit

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PreviousDrives : AppCompatActivity() {

    lateinit var dateOfDrive:String
    lateinit var timeOfDrive:String
    lateinit var driveStartedBtn: Button
    lateinit var driveEndedBtn: Button
    lateinit var listItems: ArrayList<String>
    lateinit var adapter:ArrayAdapter<String>
    lateinit var listView: ListView
    var startTime: Long =0
    lateinit var storedTime:String
    lateinit var storedDate:String
    lateinit var storedDuration:String


    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.previous_drives)
        initialize()
        listView=findViewById(R.id.list) as ListView

        adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,listItems)
        listView.setAdapter(adapter)

        driveStartedBtn=findViewById<Button>(R.id.btn_driveStart) as Button
        driveEndedBtn=findViewById<Button>(R.id.btn_driveEnd) as Button

        driveStartedBtn.setOnClickListener {
            startTime=System.currentTimeMillis()

        }

        driveEndedBtn.setOnClickListener {
            //once drive is done add date and time of drive to list
            // and then add it to the database


            //this formats the duration, date and time calculated to string form
            val endTime=System.currentTimeMillis()
            val duration=endTime-startTime
            val durationFormat=String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
            );
            val date=SimpleDateFormat("M/dd/yyyy")
            dateOfDrive=date.format(Date())
            val time=SimpleDateFormat("hh:mm:ss")
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

    private fun initialize(){
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Previous")
        mAuth = FirebaseAuth.getInstance()


        //this renders what's inside database to the list view
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //iterate over all the previous drives of that user and get there date.time.duration
                for(postSnapshot in snapshot.children){
                    storedDate = postSnapshot.child("date").value as String
                    storedTime = postSnapshot.child("time").value as String
                    storedDuration= postSnapshot.child("duration").value as String

                    listItems.add(storedDate + "/" + storedTime + "/" + storedDuration)
                    adapter.notifyDataSetChanged()
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //this makes each listView clickable and opens a new Activity that shows the information
        // of each previous drive
        listView.setOnItemClickListener { parent, view, position,id ->
            val intent = Intent(this@PreviousDrives, DriveInfo::class.java)
            startActivity(intent)
        }
    }






}