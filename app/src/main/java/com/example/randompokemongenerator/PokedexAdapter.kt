package com.example.randompokemongenerator

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PokedexAdapter(val pokemonImages: MutableList<String>, val pokemonNumbers: MutableList<String>, val pokemonTypes: MutableList<String>) : RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder>() {
    class PokedexViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pokemonImage: ImageView
        val pokemonNumber: TextView
        val pokemonType: TextView

        init {
            pokemonImage = view.findViewById(R.id.pokemon_image)
            pokemonNumber = view.findViewById(R.id.pokemon_number)
            pokemonType = view.findViewById(R.id.pokemon_type)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PokedexAdapter.PokedexViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pokedex_entry, parent, false)
        return PokedexViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pokemonImages.size
    }

    override fun onBindViewHolder(holder: PokedexAdapter.PokedexViewHolder, position: Int) {
        val pokemonImage = pokemonImages[position]

        Glide.with(holder.itemView.context)
            .load(pokemonImage).centerCrop().into(holder.pokemonImage)

        val pokemonNumber = pokemonNumbers[position]
        holder.pokemonNumber.text = pokemonNumber

        val pokemonType = pokemonTypes[position]
        holder.pokemonType.text = pokemonType
    }
}