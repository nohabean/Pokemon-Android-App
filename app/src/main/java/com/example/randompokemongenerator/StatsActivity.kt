package com.example.randompokemongenerator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.bumptech.glide.Glide

class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_stats)

        // Retrieve Pokemon data from Intent
        val pokemonImageURL = intent.getStringExtra("pokemonImageURL")
        val pokemonNumber = intent.getStringExtra("pokemonNumber")
        val pokemonName = intent.getStringExtra("pokemonName")
        val pokemonType = intent.getStringExtra("pokemonType")

        // Populate stats XML with Pokemon data
        val image = findViewById<ImageView>(R.id.pokemonImage)
        val numberTextView = findViewById<TextView>(R.id.pokemonNumber)
        val nameTextView = findViewById<TextView>(R.id.pokemonName)
        val typeTextView = findViewById<TextView>(R.id.pokemonType)

        Glide.with(this).load(pokemonImageURL).fitCenter().into(image)
        numberTextView.text = "No.$pokemonNumber"
        nameTextView.text = pokemonName
        typeTextView.text = pokemonType

        val statsButton = findViewById<Button>(R.id.statsButton)
        statsButton.setOnClickListener {
            //overridePendingTransition(0, 0)
            finish()
        }
    }
}