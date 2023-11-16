package com.example.randompokemongenerator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)

        val menuButton = findViewById<Button>(R.id.menuButton)
        menuButton.setOnClickListener {
            //overridePendingTransition(0, 0)
            finish()
        }

        val pokedexButton = findViewById<Button>(R.id.loadPokedex)
        pokedexButton.setOnClickListener {
            val intent = Intent(this@MainMenuActivity, PokedexActivity::class.java)
            startActivity(intent)
        }
    }
}