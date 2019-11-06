package com.cmsc436.vehiclefinemaps
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cmsc436.vehiclefinemaps.R.id.btn_letsDrive
import kotlinx.android.synthetic.main.choosing_option.*

class ChoosingOption : AppCompatActivity() {

    val LetsDriveBtn = findViewById<Button>(R.id.btn_letsDrive) as Button
    val ShowPreviousDrivesBtn = findViewById<Button>(R.id.btn_showprev) as Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choosing_option)

        LetsDriveBtn.setOnClickListener {

        }

        ShowPreviousDrivesBtn.setOnClickListener {

        }

    }


}