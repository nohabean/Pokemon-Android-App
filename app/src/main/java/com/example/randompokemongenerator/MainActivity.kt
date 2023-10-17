package com.example.randompokemongenerator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class MainActivity : AppCompatActivity() {
    var pokemonImageURL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPokemonImageURL()
        val button = findViewById<Button>(R.id.randomButton)
        val image = findViewById<ImageView>(R.id.pokemonImage)

        getNextImage(button, image)
    }

    private fun getPokemonImageURL() {
        val client = AsyncHttpClient()

        client["https://pokeapi.co/api/v2/pokemon/", object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Pokemon Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("Pokemon", "response successful$json")
                pokemonImageURL = json.jsonObject.getString("message")
                Log.d("pokemonImageURL", "pokemon image URL set")
            }
        }]
    }

    private fun getNextImage(button: Button, imageView: ImageView) {
        button.setOnClickListener() {
            getPokemonImageURL()
            Glide.with(this).load(pokemonImageURL).fitCenter().into(imageView)
        }
    }
}