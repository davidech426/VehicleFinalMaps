package com.cmsc436.vehiclefinemaps

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PreviousDrives : AppCompatActivity() {

    lateinit var dateOfDrive:String
    lateinit var timeOfDrive:String
    lateinit var driveStartedBtn: Button
    lateinit var driveEndedBtn: Button
    lateinit var listItems: ArrayList<String>
    lateinit var adapter:ArrayAdapter<String>
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.previous_drives)
        listView=findViewById<ListView>(R.id.list) as ListView
        val date=SimpleDateFormat("M/dd/yyyy")
        dateOfDrive=date.format(Date())
        val time=SimpleDateFormat("hh:mm:ss")
        timeOfDrive=time.format(Date())
        adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,listItems)
        listView.setAdapter(adapter)

        driveStartedBtn=findViewById<Button>(R.id.btn_driveStart) as Button
        driveEndedBtn=findViewById<Button>(R.id.btn_driveEnd) as Button

        driveStartedBtn.setOnClickListener {
                listItems.add(timeOfDrive)
                adapter.notifyDataSetChanged()
        }

        driveEndedBtn.setOnClickListener {

        }



    }

}