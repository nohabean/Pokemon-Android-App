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
                val pokemonWeight = json.jsobObject.getString("weight")
                Log.d("weight", pokemonWeight)
            }
        }]
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

                for (i in 0 until movesArray.length()) {
                    val moveObject = movesArray.getJSONObject(i)
                    val moveName = moveObject.getJSONObject("move").getString("name")

                    // Check if the move can be learned by level-up
                    val levelUpLearnMethods = moveObject.getJSONArray("version_group_details")
                        .filter { it.getJSONObject("move_learn_method").getString("name") == "level-up" }

                    // If the move can be learned by level-up, get the level learned at
                    if (levelUpLearnMethods.isNotEmpty()) {
                        val levelLearnedAt = levelUpLearnMethods.last().getInt("level_learned_at")

                        // Fetch move details using the move URL
                        val moveJSON = "https://pokeapi.co/api/v2/move/$moveName"
                        client[moveJSON, object : JsonHttpResponseHandler() {
                            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                                Log.d("Move Error", errorResponse)
                            }

                            override fun onSuccess(statusCode: Int, headers: Headers?, moveJson: JsonHttpResponseHandler.JSON) {
                                Log.d("Move success", moveJson.jsonObject.toString())

                                // Extract power, pp, and type name from the move JSON
                                val accuracy = moveJSON.jsonObject.getString("accuracy")
                                val power = moveJSON.jsonObject.getString("power")
                                val pp = moveJSON.jsonObject.getString("pp")
                                val typeName = moveJSON.jsonObject.getJSONObject("type").getString("name")

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
    
                                // Log or use the extracted move details as needed
                                Log.d("Move Details", "Move: $moveName, Level: $levelLearnedAt, Power: $power, PP: $pp, Type: $typeName")
    
                                // Update UI or perform any other actions as needed
                            }
                        }]
                    }
                }    

                // Now you have a list of maps containing move details
                // You can access individual move details as needed
                for (moveDetails in movesList) {
                    Log.d("Move Details", moveDetails.toString())
                }
            }    
        }]
    }
}
