package com.cmsc436.vehiclefinemaps
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cmsc436.vehiclefinemaps.R.id.btn_letsDrive
import kotlinx.android.synthetic.main.choosing_option.*
//import androidx.navigation.ui.AppBarConfiguration

class ChoosingOption : AppCompatActivity() {

    lateinit var LetsDriveBtn: Button
    lateinit var ShowPreviousDrivesBtn: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choosing_option)

        LetsDriveBtn = findViewById<Button>(R.id.btn_letsDrive) as Button
        ShowPreviousDrivesBtn = findViewById<Button>(R.id.btn_showprev) as Button


        LetsDriveBtn.setOnClickListener {
            //Display current drive
            val i = Intent(this@ChoosingOption, LetsDrive::class.java)
            startActivity(i)
        }

        ShowPreviousDrivesBtn.setOnClickListener {
            Log.e("I'm here","I'm here")
            val i = Intent(this@ChoosingOption,PreviousDrives::class.java)
            startActivity(i)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.profile -> {

                //TODO: THE ACTIVITY SHOULD CALL SOMETHING THAT DISPLAYS THE USER'S INFORMATION
                val i = Intent(this@ChoosingOption, LoginActivity::class.java)
                startActivity(i)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}