package com.example.randompokemongenerator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray
import kotlin.random.Random

class PokedexListActivity : AppCompatActivity() {
    private lateinit var recyclerViewPokedex: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pokedex_list)

        recyclerViewPokedex = findViewById<RecyclerView>(R.id.pokedex_recycler_view)
        val pokedexImageList = mutableListOf<String>()
        val pokedexNumberList = mutableListOf<String>()
        val pokedexTypeList = mutableListOf<String>()

        getPokemonData(1, 1017, pokedexImageList, pokedexNumberList, pokedexTypeList)
    }

    private fun getPokemonData(currentPokemonId: Int, lastPokemonId: Int, pokemonImagesList: MutableList<String>, pokemonNumberList: MutableList<String>, pokemonTypeList: MutableList<String>) {
        if (currentPokemonId <= lastPokemonId) {
            val client = AsyncHttpClient()
            val pokemonJSON = "https://pokeapi.co/api/v2/pokemon/$currentPokemonId"

            client[pokemonJSON, object : JsonHttpResponseHandler() {
                override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                    Log.d("Error", errorResponse)
                }

                override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                    Log.d("success", json.jsonObject.toString())
                    val pokemonObject = json.jsonObject
                    val pokemonNumber = pokemonObject.getString("id")
                    val sprites = json.jsonObject.getJSONObject("sprites")
                    val pokemonImage = sprites.getString("front_default")
                    val types = json.jsonObject.getJSONArray("types")
                    val pokemonType = extractTypeNames(types)

                    pokemonImagesList.add(pokemonImage)
                    pokemonNumberList.add(pokemonNumber)
                    pokemonTypeList.add(pokemonType)

                    // Continue fetching data for the next Pokemon
                    getPokemonData(currentPokemonId + 1, lastPokemonId, pokemonImagesList, pokemonNumberList, pokemonTypeList)
                }
            }]
        } else {
            Log.d("Images", pokemonImagesList.toString())
            Log.d("Numbers", pokemonNumberList.toString())
            Log.d("Types", pokemonTypeList.toString())
            // All Pokemon data fetched, update RecyclerView
            val gridLayoutManager = GridLayoutManager(this@PokedexListActivity, 2)
            recyclerViewPokedex.layoutManager = gridLayoutManager
            recyclerViewPokedex.adapter =
                PokedexAdapter(pokemonImagesList, pokemonNumberList, pokemonTypeList)
            recyclerViewPokedex.addItemDecoration(
                DividerItemDecoration(
                    this@PokedexListActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
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
}