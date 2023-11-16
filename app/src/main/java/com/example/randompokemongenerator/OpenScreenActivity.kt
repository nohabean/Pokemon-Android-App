package com.example.randompokemongenerator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OpenScreenActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.opening_screen)

        val openButton = findViewById<Button>(R.id.openButton)
        openButton.setOnClickListener {
            val intent = Intent(this@OpenScreenActivity, MainMenuActivity::class.java)
            startActivity(intent)
        }
    }
}