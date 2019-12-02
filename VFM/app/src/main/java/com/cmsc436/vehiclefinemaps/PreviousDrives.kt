package com.cmsc436.vehiclefinemaps

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlin.collections.ArrayList
import android.util.Log


import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PreviousDrives : AppCompatActivity() {


    var listItems: ArrayList<String> = ArrayList<String>()
    lateinit var adapter:ArrayAdapter<String>
    lateinit var listView: ListView
    lateinit var storedTime:String
    lateinit var storedDate:String
    lateinit var storedDuration:String


    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.previous_drives)


        listView=findViewById(R.id.list) as ListView
        initialize()

        adapter= ArrayAdapter(this@PreviousDrives,android.R.layout.simple_list_item_1,listItems)
        listView.setAdapter(adapter)

        //initialize()

    }

    private fun initialize(){
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Previous")
        mAuth = FirebaseAuth.getInstance()


        //this renders what's inside database to the list view
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //iterate over all the previous drives of that user and get there date.time.duration
                for(postSnapshot in snapshot.children){
                    if(postSnapshot.child("date").value!=null && postSnapshot.child("time").value!=null &&
                        postSnapshot.child("duration").value!=null){
                            storedDate = postSnapshot.child("date").value as String
                            storedTime = postSnapshot.child("time").value as String
                            storedDuration= postSnapshot.child("duration").value as String
                    }

                    listItems.add("Date: "+storedDate + "  Time: " + storedTime + "  " +
                            "       Duration: " + storedDuration)
                    adapter.notifyDataSetChanged()

                    //this makes each listView clickable and opens a new Activity that shows the information
                    // of each previous drive
                    listView.setOnItemClickListener { parent, view, position,id ->
                        val intent = Intent(this@PreviousDrives, DriveInfo::class.java)
                        var driveId=postSnapshot.key as String
                        //var listItem=listView.getItemAtPosition(position)
                        intent.putExtra("driveId",driveId)
                        startActivity(intent)

                    }
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }






}