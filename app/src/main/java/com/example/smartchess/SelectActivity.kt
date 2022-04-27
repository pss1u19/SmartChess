package com.example.smartchess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SelectActivity : AppCompatActivity() {
    var trainingTime :Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        val switch = findViewById<Switch>(R.id.colorSwitch)
        val button = findViewById<Button>(R.id.startButton)

        val rbGroup = findViewById<RadioGroup>(R.id.radio)

        //val s = Sequential.of(Input(8,8,12),Flatten())

        findViewById<Button>(R.id.trainButton).setOnClickListener{
            val ai1 = AI("ai1")
            val newThread = Thread()
            newThread.run { ai1.train() }

        }
        button.setOnClickListener {
            val checkedButton = rbGroup.checkedRadioButtonId
            println(System.nanoTime())
            val playIntent = Intent(this, GameActivity::class.java)

            playIntent.putExtra("Colour", switch.isChecked)
            startActivity(playIntent)
        }

    }
}