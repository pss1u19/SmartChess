package com.example.smartchess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch

class SelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        val switch = findViewById<Switch>(R.id.colorSwitch)
        val button = findViewById<Button>(R.id.startButton)
        button.setOnClickListener {
            val playIntent = Intent(this, GameActivity::class.java)
            playIntent.putExtra("Colour", switch.isChecked)
            startActivity(playIntent)
        }
    }
}