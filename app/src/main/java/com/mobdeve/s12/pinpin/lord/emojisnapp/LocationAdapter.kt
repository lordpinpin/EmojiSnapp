package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.GameLocationBinding

class LocationAdapter (
    private val locations: List<Location>,
    private val onEmojiClick: (Emoji) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    init {
        // Log to verify the locations data
        Log.d("LocationAdapter", "Locations size: ${locations.size}")
    }

    inner class LocationViewHolder(val binding: GameLocationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(location: Location) {
            binding.locationIconTx.text = location.icon
            binding.locationNameTx.text = location.name
            binding.locationDescTx.text = location.description

            // Set up player emoji RecyclerView if not already done
            val playerEmojiAdapter = GameEmojiAdapter(DataGenerator.loadFourEmojis()) { emoji ->
                onEmojiClick(emoji)
            }
            binding.playerEmojiRv.adapter = playerEmojiAdapter

            // Set up opponent emoji RecyclerView if not already done
            val oppEmojiAdapter = GameEmojiAdapter(DataGenerator.loadThreeEmojis()) { emoji ->
                onEmojiClick(emoji)
            }
            binding.oppEmojiRv.adapter = oppEmojiAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = GameLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // Set the layout manager for both RecyclerViews here to avoid repeating in bind()
        binding.playerEmojiRv.layoutManager = GridLayoutManager(parent.context, 2)
        binding.oppEmojiRv.layoutManager = GridLayoutManager(parent.context, 2)

        // Set fixed size for performance
        binding.playerEmojiRv.setHasFixedSize(true)
        binding.oppEmojiRv.setHasFixedSize(true)

        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount(): Int = locations.size
}