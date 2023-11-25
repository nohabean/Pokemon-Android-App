package com.example.randompokemongenerator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        val menuButton = findViewById<Button>(R.id.menuButton)
        val litUpButton = findViewById<Button>(R.id.litMenuButton)

        menuButton.setOnClickListener {
            // Toggle visibility to show the "lit up" button
            litUpButton.visibility = View.VISIBLE
            menuButton.visibility = View.INVISIBLE

            // Delay for a brief period
            Handler(Looper.getMainLooper()).postDelayed({
                // Toggle visibility back to the original button
                litUpButton.visibility = View.INVISIBLE
                menuButton.visibility = View.VISIBLE

                overridePendingTransition(0, 0)
                // Other actions you want to perform on button click
                finish()
            }, 200) // Adjust the duration as needed
        }

        val pokedexButton = findViewById<Button>(R.id.loadPokedex)
        pokedexButton.setOnClickListener {
            val intent = Intent(this@MainMenuActivity, PokedexActivity::class.java)
            startActivity(intent)
        }
    }
}