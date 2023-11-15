package com.example.randompokemongenerator

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import androidx.core.util.Pair
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var pokemonImageURL = ""
    var pokemonNumber = ""
    var pokemonName = ""
    var pokemonType = ""

    var currentPokemonNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val menuButton = findViewById<Button>(R.id.menuButton)
        //menuButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3498db"))

        val randomButton = findViewById<Button>(R.id.randomButton)
        val image = findViewById<ImageView>(R.id.pokemonImage)
        val pokedexNumber = findViewById<TextView>(R.id.pokemonNumber)
        val name = findViewById<TextView>(R.id.pokemonName)
        val type = findViewById<TextView>(R.id.pokemonType)
        val nextButton = findViewById<Button>(R.id.nextButton)
        val previousButton = findViewById<Button>(R.id.previousButton)
        val showStatsButton = findViewById<Button>(R.id.statsButton)

        randomButton.setOnClickListener {
            // Fetch random Pokemon data
            getRandomPokemon(image, pokedexNumber, name, type)
        }

        nextButton.setOnClickListener {
            // Fetch next Pokemon data based on the last generated random Pokemon
            getNextPokemon(image, pokedexNumber, name, type)
        }

        previousButton.setOnClickListener {
            // Fetch previous Pokemon data based on the last generated random Pokemon
            getPreviousPokemon(image, pokedexNumber, name, type)
        }

        showStatsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, StatsActivity::class.java).apply {
                putExtra("pokemonImageURL", pokemonImageURL)
                putExtra("pokemonNumber", pokemonNumber)
                putExtra("pokemonName", pokemonName)
                putExtra("pokemonType", pokemonType)
            }
            //overridePendingTransition(0, 0)
            startActivity(intent)
        }

        val searchView = findViewById<SearchView>(R.id.searchBar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val isNumeric = query?.toIntOrNull() != null

                if (!query.isNullOrBlank()) {
                    if (isNumeric) {
                        // The query is a numeric ID, so search by ID
                        val id = query.toInt()
                        getPokemonDataByID(id, image, pokedexNumber, name, type)
                    } else {
                        // The query is not a numeric ID, so search by name
                        getPokemonDataByName(query, image, pokedexNumber, name, type)
                    }

                    searchView.setQuery("", false)
                    searchView.clearFocus() // Hide the keyboard
                    searchView.isIconified = true
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes in the search view (optional)
                return true
            }
        })
    }

    private fun getRandomPokemon(imageView: ImageView, numberTextView: TextView, nameTextView: TextView, typeTextView: TextView) {
        val client = AsyncHttpClient()
        var rand = Random.nextInt(1017)
        val pokemonJSON = "https://pokeapi.co/api/v2/pokemon/$rand"

        client[pokemonJSON, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Image Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("success", json.jsonObject.toString())
                val sprites = json.jsonObject.getJSONObject("sprites")
                pokemonImageURL = sprites.getString("front_default")
                pokemonNumber = json.jsonObject.getString("id")
                pokemonName = json.jsonObject.getString("name")
                val types = json.jsonObject.getJSONArray("types")
                val typeNames = extractTypeNames(types)
                pokemonType = typeNames

                // Display Pokemon data
                Glide.with(this@MainActivity).load(pokemonImageURL).fitCenter().into(imageView)
                numberTextView.text = "No.$pokemonNumber"
                nameTextView.text = pokemonName
                typeTextView.text = pokemonType

                currentPokemonNumber = pokemonNumber
            }
        }]
    }

    private fun getNextPokemon(imageView: ImageView, numberTextView: TextView, nameTextView: TextView, typeTextView: TextView) {
        // If the current pokemon number is null or empty, set it to 0
        val nextPokemonNumber = currentPokemonNumber.toInt() + 1
        val pokemonJSON = "https://pokeapi.co/api/v2/pokemon/$nextPokemonNumber"

        val client = AsyncHttpClient()
        client[pokemonJSON, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Image Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("success", json.jsonObject.toString())
                val sprites = json.jsonObject.getJSONObject("sprites")
                pokemonImageURL = sprites.getString("front_default")
                pokemonNumber = json.jsonObject.getString("id")
                pokemonName = json.jsonObject.getString("name")
                val types = json.jsonObject.getJSONArray("types")
                val typeNames = extractTypeNames(types)
                pokemonType = typeNames

                // Display Pokemon data
                Glide.with(this@MainActivity).load(pokemonImageURL).fitCenter().into(imageView)
                numberTextView.text = "No.$pokemonNumber"
                nameTextView.text = pokemonName
                typeTextView.text = pokemonType

                currentPokemonNumber = pokemonNumber
            }
        }]
    }

    private fun getPreviousPokemon(imageView: ImageView, numberTextView: TextView, nameTextView: TextView, typeTextView: TextView) {
        // If the current pokemon number is null or empty, set it to 2
        val previousPokemonNumber = currentPokemonNumber.toInt() - 1
        val pokemonJSON = "https://pokeapi.co/api/v2/pokemon/$previousPokemonNumber"

        val client = AsyncHttpClient()
        client[pokemonJSON, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Image Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("success", json.jsonObject.toString())
                val sprites = json.jsonObject.getJSONObject("sprites")
                pokemonImageURL = sprites.getString("front_default")
                pokemonNumber = json.jsonObject.getString("id")
                pokemonName = json.jsonObject.getString("name")
                val types = json.jsonObject.getJSONArray("types")
                val typeNames = extractTypeNames(types)
                pokemonType = typeNames

                // Display Pokemon data
                Glide.with(this@MainActivity).load(pokemonImageURL).fitCenter().into(imageView)
                numberTextView.text = "No.$pokemonNumber"
                nameTextView.text = pokemonName
                typeTextView.text = pokemonType

                currentPokemonNumber = pokemonNumber
            }
        }]
    }

    private fun extractTypeNames(typesArray: JSONArray): String {
        val typeNames = mutableListOf<String>()
        for (i in 0 until typesArray.length()) {
            val typeObj = typesArray.getJSONObject(i).getJSONObject("type")
            val typeName = typeObj.getString("name")
            typeNames.add(typeName)
        }
        return typeNames.joinToString(" / ")
    }

    private fun getPokemonDataByName(name: String, image: ImageView, numberTextView: TextView, nameTextView: TextView, typeTextView: TextView) {
        // Construct the URL based on the Pokémon name and fetch data from the API
        val lowercaseName = name.lowercase()
        val client = AsyncHttpClient()
        val pokemonJSON = "https://pokeapi.co/api/v2/pokemon/$lowercaseName"

        client[pokemonJSON, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Search Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                val sprites = json.jsonObject.getJSONObject("sprites")
                pokemonImageURL = sprites.getString("front_default")
                pokemonNumber = json.jsonObject.getString("id")
                pokemonName = json.jsonObject.getString("name")
                val types = json.jsonObject.getJSONArray("types")
                val typeNames = extractTypeNames(types)
                pokemonType = typeNames

                // Display Pokémon data
                Glide.with(this@MainActivity).load(pokemonImageURL).fitCenter().into(image)
                numberTextView.text = "No.$pokemonNumber"
                nameTextView.text = pokemonName
                typeTextView.text = pokemonType

                currentPokemonNumber = pokemonNumber
            }
        }]
    }

    private fun getPokemonDataByID(id: Int, imageView: ImageView, numberTextView: TextView, nameTextView: TextView, typeTextView: TextView) {
        val client = AsyncHttpClient()
        val pokemonURL = "https://pokeapi.co/api/v2/pokemon/$id"

        client.get(pokemonURL, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Pokemon Data Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("Success", json.jsonObject.toString())

                val sprites = json.jsonObject.getJSONObject("sprites")
                pokemonImageURL = sprites.getString("front_default")
                pokemonNumber = json.jsonObject.getString("id")
                pokemonName = json.jsonObject.getString("name")
                val types = json.jsonObject.getJSONArray("types")
                val typeNames = extractTypeNames(types)
                pokemonType = typeNames

                // Update the UI elements
                Glide.with(this@MainActivity).load(pokemonImageURL).fitCenter().into(imageView)
                numberTextView.text = "No.$pokemonNumber"
                nameTextView.text = pokemonName
                typeTextView.text = pokemonType

                currentPokemonNumber = pokemonNumber
            }
        })
    }
}