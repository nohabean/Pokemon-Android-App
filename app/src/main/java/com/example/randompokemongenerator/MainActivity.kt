package com.example.randompokemongenerator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var pokemonImageURL = ""
    var pokemonNumber = ""
    var pokemonPower = ""
    var pokemonName = ""
    var pokemonType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPokemonImageURL()
        val button = findViewById<Button>(R.id.randomButton)
        val image = findViewById<ImageView>(R.id.pokemonImage)
        val pokedexNumber = findViewById<TextView>(R.id.pokemonNumber)
        val power = findViewById<TextView>(R.id.pokemonPower)
        val name = findViewById<TextView>(R.id.pokemonName)
        val type = findViewById<TextView>(R.id.pokemonType)

        getNextImage(button, image, pokedexNumber, power, name, type)

        val searchView = findViewById<SearchView>(R.id.searchBar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val isNumeric = query?.toIntOrNull() != null

                if (!query.isNullOrBlank()) {
                    if (isNumeric) {
                        // The query is a numeric ID, so search by ID
                        val id = query.toInt()
                        getPokemonDataByID(id, image, pokedexNumber, power, name, type)
                    } else {
                        // The query is not a numeric ID, so search by name
                        getPokemonDataByName(query, image, pokedexNumber, power, name, type)
                    }
                    searchView.clearFocus() // Hide the keyboard
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes in the search view (optional)
                return true
            }
        })
    }

    private fun getPokemonImageURL() {
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
                Log.d("pokemonImageURL", "pokemon image URL set")
                pokemonNumber = json.jsonObject.getString("id")
                Log.d("pokemonNumber", "pokemon number set")
                pokemonPower = json.jsonObject.getString("base_experience")
                Log.d("pokemonPower", "pokemon power set")
                pokemonName = json.jsonObject.getString("name")
                Log.d("pokemonName", "pokemon name set")
                val types = json.jsonObject.getJSONArray("types")
                val typeNames = extractTypeNames(types)
                pokemonType = typeNames
                Log.d("pokemonType", "pokemon type set")
            }
        }]
    }

    private fun getNextImage(button: Button, imageView: ImageView, numberTextView: TextView, powerTextView: TextView, nameTextView: TextView, typeTextView: TextView) {
        button.setOnClickListener() {
            getPokemonImageURL()
            Glide.with(this).load(pokemonImageURL).fitCenter().into(imageView)
            numberTextView.text = "No. $pokemonNumber"
            powerTextView.text = if (pokemonPower != "null") pokemonPower else ""
            nameTextView.text = pokemonName
            typeTextView.text = pokemonType
        }
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

    private fun getPokemonDataByName(name: String, image: ImageView, numberTextView: TextView, powerTextView: TextView, nameTextView: TextView, typeTextView: TextView) {
        // Construct the URL based on the Pokémon name and fetch data from the API
        val client = AsyncHttpClient()
        val pokemonJSON = "https://pokeapi.co/api/v2/pokemon/$name"

        client[pokemonJSON, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Search Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                val sprites = json.jsonObject.getJSONObject("sprites")
                pokemonImageURL = sprites.getString("front_default")
                pokemonNumber = json.jsonObject.getString("id")
                pokemonPower = json.jsonObject.getString("base_experience")
                pokemonName = json.jsonObject.getString("name")
                val types = json.jsonObject.getJSONArray("types")
                val typeNames = extractTypeNames(types)
                pokemonType = typeNames

                // Display Pokémon data
                Glide.with(this@MainActivity).load(pokemonImageURL).fitCenter().into(image)
                numberTextView.text = "No. $pokemonNumber"
                powerTextView.text = if (pokemonPower != "null") pokemonPower else ""
                nameTextView.text = pokemonName
                typeTextView.text = pokemonType
            }
        }]
    }

    private fun getPokemonDataByID(id: Int, imageView: ImageView, numberTextView: TextView, powerTextView: TextView, nameTextView: TextView, typeTextView: TextView) {
        val client = AsyncHttpClient()
        val pokemonURL = "https://pokeapi.co/api/v2/pokemon/$id"

        client.get(pokemonURL, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Pokemon Data Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("Success", json.jsonObject.toString())

                val sprites = json.jsonObject.getJSONObject("sprites")
                val pokemonImageURL = sprites.getString("front_default")
                val pokemonNumber = json.jsonObject.getString("id")
                val pokemonPower = json.jsonObject.getString("base_experience")
                val pokemonName = json.jsonObject.getString("name")
                val types = json.jsonObject.getJSONArray("types")
                val pokemonType = extractTypeNames(types)

                // Update the UI elements
                Glide.with(this@MainActivity).load(pokemonImageURL).fitCenter().into(imageView)
                numberTextView.text = "No. $pokemonNumber"
                powerTextView.text = pokemonPower
                nameTextView.text = pokemonName
                typeTextView.text = pokemonType
            }
        })
    }
}