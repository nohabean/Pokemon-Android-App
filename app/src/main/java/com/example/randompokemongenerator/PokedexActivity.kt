package com.example.randompokemongenerator

import androidx.core.content.ContextCompat
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray
import kotlin.random.Random

class PokedexActivity : AppCompatActivity() {
    var pokemonImageURL = ""
    var pokemonNumber = ""
    var pokemonName = ""
    lateinit var pokemonType: MutableList<String>
    lateinit var typeContainer: LinearLayout

    var currentPokemonNumber: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pokedex_search)

        typeContainer = findViewById(R.id.typeContainer)

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
            val intent = Intent(this@PokedexActivity, StatsActivity::class.java).apply {
                putExtra("pokemonImageURL", pokemonImageURL)
                putExtra("pokemonNumber", pokemonNumber)
                putExtra("pokemonName", pokemonName)
                putStringArrayListExtra("pokemonType", ArrayList(pokemonType))
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
                pokemonName = json.jsonObject.getString("name").replace("-", " ")
                pokemonType = extractTypeNames(json.jsonObject.getJSONArray("types"))

                // Call the helper method to display Pokemon data
                displayPokemonData(imageView, numberTextView, nameTextView)
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
                pokemonName = json.jsonObject.getString("name").replace("-", " ")
                pokemonType = extractTypeNames(json.jsonObject.getJSONArray("types"))

                // Call the helper method to display Pokemon data
                displayPokemonData(imageView, numberTextView, nameTextView)
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
                pokemonName = json.jsonObject.getString("name").replace("-", " ")
                pokemonType = extractTypeNames(json.jsonObject.getJSONArray("types"))

                // Call the helper method to display Pokemon data
                displayPokemonData(imageView, numberTextView, nameTextView)
            }
        }]
    }

    private fun extractTypeNames(typesArray: JSONArray): MutableList<String> {
        val typeNames = mutableListOf<String>()
        for (i in 0 until typesArray.length()) {
            val typeObj = typesArray.getJSONObject(i).getJSONObject("type")
            val typeName = typeObj.getString("name")
            typeNames.add(typeName)
        }
        return typeNames
    }

    private fun getPokemonDataByName(name: String, imageView: ImageView, numberTextView: TextView, nameTextView: TextView, typeTextView: TextView) {
        // Construct the URL based on the Pokémon name and fetch data from the API
        val lowercaseName = name.lowercase()
        val client = AsyncHttpClient()
        val pokemonJSON = "https://pokeapi.co/api/v2/pokemon/$lowercaseName"

        client[pokemonJSON, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Search Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("Success", json.jsonObject.toString())
                val sprites = json.jsonObject.getJSONObject("sprites")
                pokemonImageURL = sprites.getString("front_default")
                pokemonNumber = json.jsonObject.getString("id")
                pokemonName = json.jsonObject.getString("name").replace("-", " ")
                pokemonType = extractTypeNames(json.jsonObject.getJSONArray("types"))

                // Call the helper method to display Pokemon data
                displayPokemonData(imageView, numberTextView, nameTextView)
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
                pokemonName = json.jsonObject.getString("name").replace("-", " ")
                pokemonType = extractTypeNames(json.jsonObject.getJSONArray("types"))

                // Call the helper method to display Pokemon data
                displayPokemonData(imageView, numberTextView, nameTextView)
            }
        })
    }

    private fun displayPokemonData(imageView: ImageView, numberTextView: TextView, nameTextView: TextView) {
        // Display Pokemon data
        Glide.with(this@PokedexActivity).load(pokemonImageURL).fitCenter().into(imageView)
        numberTextView.text = "No.$pokemonNumber"
        nameTextView.text = pokemonName

        // Map types to drawable resource IDs
        val typeDrawables = mapTypesToDrawables(pokemonType)

        // Clear existing views in typeContainer
        typeContainer.removeAllViews()

        // Create ImageViews for each type and add them to typeContainer
        typeDrawables.forEach { drawableId ->
            val typeImageView = ImageView(this@PokedexActivity)
            typeImageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            typeImageView.setImageResource(drawableId)
            typeImageView.scaleType = ImageView.ScaleType.FIT_CENTER
            typeContainer.addView(typeImageView)
        }

        currentPokemonNumber = pokemonNumber
    }

    private fun mapTypesToDrawables(types: List<String>): List<Int> {
        // Define a mapping of type strings to drawable resource IDs
        val typeDrawableMap = mapOf(
            "bug" to R.drawable.bug_tag,
            "dark" to R.drawable.dark_tag,
            "dragon" to R.drawable.dragon_tag,
            "electric" to R.drawable.electric_tag,
            "fairy" to R.drawable.fairy_tag,
            "fighting" to R.drawable.fighting_tag,
            "fire" to R.drawable.fire_tag,
            "flying" to R.drawable.flying_tag,
            "ghost" to R.drawable.ghost_tag,
            "grass" to R.drawable.grass_tag,
            "ground" to R.drawable.ground_tag,
            "ice" to R.drawable.ice_tag,
            "normal" to R.drawable.normal_tag,
            "poison" to R.drawable.poison_tag,
            "psychic" to R.drawable.psychic_tag,
            "rock" to R.drawable.rock_tag,
            "steel" to R.drawable.steel_tag,
            "water" to R.drawable.water_tag
        )

        // Map each type to its corresponding drawable resource ID
        return types.map { typeDrawableMap.getValue(it) }
    }
}