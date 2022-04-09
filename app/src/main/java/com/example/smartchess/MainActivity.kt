package com.example.smartchess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playButton = findViewById<Button>(R.id.playButton)
        playButton.setOnClickListener{
            val selectIntent = Intent(this,SelectActivity::class.java)
            startActivity(selectIntent)

        }
        val optionsButton = findViewById<Button>(R.id.optionsButton)
        val quitButton = findViewById<Button>(R.id.quitButton)
        quitButton.setOnClickListener {
            finishActivity(0)
            System.exit(0)
        }
    }
}