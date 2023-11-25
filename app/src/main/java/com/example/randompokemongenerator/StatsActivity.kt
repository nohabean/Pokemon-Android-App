package com.example.randompokemongenerator

import kotlin.collections.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class StatsActivity : AppCompatActivity() {

    private lateinit var typeContainer: LinearLayout  // Declare typeContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_stats)

        // Retrieve Pokemon data from Intent
        val pokemonImageURL = intent.getStringExtra("pokemonImageURL")
        val pokemonNumber = intent.getStringExtra("pokemonNumber")
        val pokemonName = intent.getStringExtra("pokemonName")
        val pokemonType: List<String> = intent.getStringArrayListExtra("pokemonType")!!

        if (pokemonNumber != null) {
            Log.d("loaded number", pokemonNumber)
        }

        // Populate stats XML with Pokemon data
        val image = findViewById<ImageView>(R.id.pokemonImage)
        val numberTextView = findViewById<TextView>(R.id.pokemonNumber)
        val nameTextView = findViewById<TextView>(R.id.pokemonName)

        Log.d("Number", numberTextView.text.toString())

        if (pokemonNumber != null) {
            getPokemonDims(pokemonNumber)
        }

        Glide.with(this).load(pokemonImageURL).fitCenter().into(image)
        numberTextView.text = "No.$pokemonNumber"
        nameTextView.text = pokemonName

        typeContainer = findViewById(R.id.typeContainer)

        // Map types to drawable resource IDs
        val typeDrawables = mapTypesToDrawables(pokemonType)

        // Clear existing views in typeContainer
        typeContainer.removeAllViews()

        // Create ImageViews for each type and add them to typeContainer
        typeDrawables.forEachIndexed { index, drawableId ->
            val typeImageView = ImageView(this@StatsActivity)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // Set layout gravity for typeImageView
            typeImageView.layoutParams = layoutParams
            typeImageView.setImageResource(drawableId)
            typeImageView.adjustViewBounds = true

            // Check if there's only one type
            if (typeDrawables.size == 1) {
                typeImageView.scaleType = ImageView.ScaleType.FIT_START
            } else {
                typeImageView.scaleType = ImageView.ScaleType.FIT_END
            }

            typeContainer.addView(typeImageView)
        }

        val backButton = findViewById<Button>(R.id.menuButton)
        val litUpButton = findViewById<Button>(R.id.litMenuButton)
        backButton.setOnClickListener {
            // Toggle visibility to show the "lit up" button
            litUpButton.visibility = View.VISIBLE
            backButton.visibility = View.INVISIBLE

            // Delay for a brief period
            Handler(Looper.getMainLooper()).postDelayed({
                // Toggle visibility back to the original button
                litUpButton.visibility = View.INVISIBLE
                backButton.visibility = View.VISIBLE

                overridePendingTransition(0, 0)
                // Other actions you want to perform on button click
                finish()
            }, 200) // Adjust the duration as needed
        }
    }

    private fun getPokemonDims(id: String) {
        val pokemonJSON = "https://pokeapi.co/api/v2/pokemon/$id"

        val client = AsyncHttpClient()
        client[pokemonJSON, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Image Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("success", json.jsonObject.toString())

                val pokemonHeight = json.jsonObject.getString("height")
                Log.d("height", pokemonHeight)
                val pokemonWeight = json.jsonObject.getString("weight")
                Log.d("weight", pokemonWeight)

                val heightTextView = findViewById<TextView>(R.id.pokemonHeight)
                val weightTextView = findViewById<TextView>(R.id.pokemonWeight)

                heightTextView.text = "Height: $pokemonHeight"
                weightTextView.text = "Weight: $pokemonWeight"
            }
        }]
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

    private fun getEvolutionData(id: String) {

    }

    private fun getVersions(id: String) {

    }

    private fun getBasePokemonStats(id: String) {
        val pokemonJSON = "https://pokeapi.co/api/v2/pokemon/$id"

        val client = AsyncHttpClient()
        client[pokemonJSON, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Image Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("success", json.jsonObject.toString())
                val baseStatsArray = json.jsonObject.getJSONArray("stats")

                var baseStatsHP = 0
                var baseStatsATK = 0
                var baseStatsDEF = 0
                var baseStatsSPD = 0
                var baseStatsSPATK = 0
                var baseStatsSPDEF = 0

                // Iterate through the stats array
                for (i in 0 until baseStatsArray.length()) {
                    val statObject = baseStatsArray.getJSONObject(i)
                    val statName = statObject.getJSONObject("stat").getString("name")
                    val baseStatValue = statObject.getInt("base_stat")

                    // Assign base stat values based on the stat name
                    when (statName) {
                        "hp" -> baseStatsHP = baseStatValue
                        "attack" -> baseStatsATK = baseStatValue
                        "defense" -> baseStatsDEF = baseStatValue
                        "speed" -> baseStatsSPD = baseStatValue
                        "special-attack" -> baseStatsSPATK = baseStatValue
                        "special-defense" -> baseStatsSPDEF = baseStatValue
                    }
                }
                Log.d("Base Stats", "HP: $baseStatsHP, ATK: $baseStatsATK, DEF: $baseStatsDEF, SPD: $baseStatsSPD, SPATK: $baseStatsSPATK, SPDEF: $baseStatsSPDEF")
            }
        }]
    }

    private fun getStrengthsWeaknesses(id: String) {

    }

    /*private fun getMoves(id: String) {
        val pokemonJSON = "https://pokeapi.co/api/v2/pokemon/$id"

        val client = AsyncHttpClient()
        client[pokemonJSON, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Image Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("success", json.jsonObject.toString())

                // Get the moves array
                val movesArray = json.jsonObject.getJSONArray("moves")

                // Create lists to store move names and their corresponding levels
                val moveNames = mutableListOf<String>()
                val levelsLearnedAt = mutableListOf<Int>()

                // Iterate through the moves array
                for (i in 0 until movesArray.length()) {
                    val moveObject = movesArray.getJSONObject(i)
                    val moveName = moveObject.getJSONObject("move").getString("name")

                    val moveJSON = "https://pokeapi.co/api/v2/move/$moveName"
                    client[moveJSON, object : JsonHttpResponseHandler() {
                        override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                            Log.d("Image Error", errorResponse)
                        }

                        override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                            Log.d("success", json.jsonObject.toString())

                        }
                    }]

                    // Check if the move can be learned by level-up
                    val levelUpLearnMethods = moveObject.getJSONArray("version_group_details")
                        .filter { it.getJSONObject("move_learn_method").getString("name") == "level-up" }

                    // If the move can be learned by level-up, get the level learned at
                    if (levelUpLearnMethods.isNotEmpty()) {
                        val levelLearnedAt = levelUpLearnMethods.last().getInt("level_learned_at")

                        // Add move name and level to the lists
                        moveNames.add(moveName)
                        levelsLearnedAt.add(levelLearnedAt)
                    }
                }
                // Now you have two lists: moveNames and levelsLearnedAt
                // You can access individual move details as needed
                for (i in moveNames.indices) {
                    Log.d("Move", "${moveNames[i]} learned at level ${levelsLearnedAt[i]}")
                }
                // Update UI or perform any other actions as needed
            }
        }]
    }*/

    private fun getMoves(id: String) {
        val pokemonJSON = "https://pokeapi.co/api/v2/pokemon/$id"

        val client = AsyncHttpClient()
        client[pokemonJSON, object : JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Image Error", errorResponse)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("success", json.jsonObject.toString())

                val movesArray = json.jsonObject.getJSONArray("moves")
                // Create a list to store move details
                val movesList = mutableListOf<Map<String, Any>>()

                """for (i in 0 until movesArray.length()) {
                    val moveObject = movesArray.getJSONObject(i)
                    val moveName = moveObject.getJSONObject("move").getString("name")

                    // Check if the move can be learned by level-up
                    val levelUpLearnMethods = moveObject.getJSONArray("version_group_details")
                        .filter { it.getJSONObject("move_learn_method").getString("name") == "level-up" }

                    // If the move can be learned by level-up, get the level learned at
                    if (levelUpLearnMethods.isNotEmpty()) {
                        val levelLearnedAt = levelUpLearnMethods.last().getInt("level_learned_at")

                        // Fetch move details using the move URL
                        val moveJSON = "https://pokeapi.co/api/v2/move/moveName"
                        client[moveJSON, object : JsonHttpResponseHandler() {
                            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                                Log.d("Move Error", errorResponse)
                            }

                            override fun onSuccess(statusCode: Int, headers: Headers?, moveJson: JsonHttpResponseHandler.JSON) {
                                Log.d("Move success", moveJson.jsonObject.toString())

                                // Extract power, pp, and type name from the move JSON
                                val accuracy = json.jsonObject.getString("accuracy")
                                val power = json.jsonObject.getString("power")
                                val pp = json.jsonObject.getString("pp")
                                val typeName = json.jsonObject.getJSONObject("type").getString("name")

                                // Create a map with move details
                                val moveDetails = mapOf(
                                    "movename" to moveName,
                                    "level" to levelLearnedAt,
                                    "accuracy" to accuracy,
                                    "power" to power,
                                    "pp" to pp,
                                    "typename" to typeName
                                )

                                // Add the map to the list
                                movesList.add(moveDetails)
                                
                                // Update UI or perform any other actions as needed
                            }
                        }]
                    }
                }

                // Now you have a list of maps containing move details
                // You can access individual move details as needed
                for (moveDetails in movesList) {
                    Log.d("Move Details", moveDetails.toString())
                }"""
            }
        }]
    }
}