package com.cmsc436.vehiclefinemaps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView


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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drive_info)
        initialise()
    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Previous")
        mAuth = FirebaseAuth.getInstance()
        tvDate = findViewById<View>(R.id.tv_date) as TextView
        tvTime = findViewById<View>(R.id.tv_time) as TextView
        tvAvgSpeed = findViewById<View>(R.id.tv_avgspeed) as TextView
        tvLikelihood = findViewById<View>(R.id.tv_likelihood) as TextView
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        //need to get the id of the sub table for the drive from previous drives
        val mDriveReference= mUserReference.child("the drive id")
        mDriveReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvDate!!.text = snapshot.child("date").value as String
                tvTime!!.text = snapshot.child("time").value as String
                //tvLikelihood!!.text = snapshot.child("likelihood").value as String
               // tvAvgSpeed!!.text = snapshot.child("average speed").value as String
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}