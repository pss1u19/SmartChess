package com.example.smartchess

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        val switch = findViewById<Switch>(R.id.colorSwitch)
        val button = findViewById<Button>(R.id.startButton)
        var trainingThread = Thread()
        var isTraining = false
        val rbGroup = findViewById<RadioGroup>(R.id.radio)
        val tracker = findViewById<TextView>(R.id.tracker)
        val ai1 = AI("ai1")
        try {
            ai1.load()
        } catch (e: Error) {
            try {
                ai1.newModel()
            } catch (e: Error) {
            }
        }
        catch(e:Exception){}
        val ai2 = AI("ai2")
        try {
            ai2.load()
        } catch (e: Error) {
            try {
                ai2.newModel()
            } catch (e: Error) {
            }
        }catch(e:Exception){}
        val ai3 = AI("ai3")
        try {
            ai3.load()
        } catch (e: Error) {
            try {
                ai3.newModel()
            } catch (e: Error) {
            }
        }catch(e:Exception){}
        tracker.setOnClickListener {
            if (isTraining) {
                (it as TextView).setText("Training in progress")
            } else {
                (it as TextView).setText("Done")
            }
        }
        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            try{
            ai1.newModel()
            ai1.save()}catch (e:Exception){}catch (e:Error){}
        }
        findViewById<Button>(R.id.deleteButton2).setOnClickListener {
            try{
                ai2.newModel()
                ai2.save()}catch (e:Exception){}catch (e:Error){}
        }
        findViewById<Button>(R.id.deleteButton3).setOnClickListener {
            try{
                ai3.newModel()
                ai3.save()}catch (e:Exception){}catch (e:Error){}
        }
        findViewById<Button>(R.id.trainButton).setOnClickListener {
            try {
                trainingThread = Thread {
                    isTraining = true
                    ai1.train()
                    isTraining = false
                    ai1.save()
                }
                trainingThread.start()
                tracker.setText("Training in progress")
            } catch (e: Exception) {
            }catch (e:Error){}

        }
        findViewById<Button>(R.id.trainButton2).setOnClickListener {

            try {
                trainingThread = Thread({
                    isTraining = true
                    ai2.train()
                    isTraining = false
                    ai2.save()
                })
                trainingThread.start()
                tracker.setText("Training in progress")
            } catch (e: Exception) {
            }catch (e:Error){}

        }
        findViewById<Button>(R.id.trainButton3).setOnClickListener {
            try {
                trainingThread = Thread({
                    isTraining = true
                    ai3.train()
                    isTraining = false
                    ai3.save()
                })
                trainingThread.start()
                tracker.setText("Training in progress")

            } catch (e: java.lang.Exception) {
            }catch (e:Error){}

        }

        button.setOnClickListener {
            if (!trainingThread.isAlive) {
                val checkedButton = findViewById<RadioButton>(rbGroup.checkedRadioButtonId)

                println(System.nanoTime())
                val playIntent = Intent(this, GameActivity::class.java)
                when(checkedButton.text.last()){
                    'I' ->{
                        playIntent.putExtra("AI",0)
                    }
                    '1' ->{
                        playIntent.putExtra("AI",1)
                    }
                    '2' ->{
                        playIntent.putExtra("AI",2)
                    }
                    '3' ->{
                        playIntent.putExtra("AI",3)
                    }
                }
                playIntent.putExtra("Colour", switch.isChecked)
                startActivity(playIntent)
            }
        }

    }
}