package com.cmsc436.vehiclefinemaps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Button


import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DriveInfo : AppCompatActivity() {

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    //UI elements
    private var tvDate: TextView? = null
    private var tvTime: TextView? = null
    private var tvAvgSpeed: TextView? = null
    private var tvLikelihood: TextView? = null
    lateinit var DeleteBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drive_info)

        initialise()

        DeleteBtn = findViewById<Button>(R.id.delete_button) as Button

        DeleteBtn.setOnClickListener {
            val mUser = mAuth!!.currentUser
            val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
            //delete the node (the specific drive) from the current user in firebase database
            val driveId=intent?.extras!!.getString("driveId")
            mUserReference.child(driveId).removeValue()

        }


    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Previous")
        mAuth = FirebaseAuth.getInstance()
        tvDate = findViewById<View>(R.id.tv_date) as TextView
        tvTime = findViewById<View>(R.id.tv_time) as TextView
        tvAvgSpeed = findViewById<View>(R.id.tv_avgspeed) as TextView
        tvLikelihood = findViewById<View>(R.id.tv_likelihood) as TextView


        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        //need to get the id of the sub table for the drive from previous drives
        val prevDriveId=intent?.extras!!.getString("driveId")

        val mDriveReference= mUserReference.child(prevDriveId)
        mDriveReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("date").value!=null && snapshot.child("time").value!=null){
                    tvDate!!.text = snapshot.child("date").value as String
                    tvTime!!.text = snapshot.child("time").value as String
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }



}