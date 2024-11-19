package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.GameLocationBinding
class LocationAdapter(
    private var locations: List<Location>,
    private var playerEmojis: MutableList<MutableList<Emoji>>,
    private var oppEmojis: MutableList<MutableList<Emoji>>,
    private var playerTotals: List<Int>,
    private var oppTotals: List<Int>,
    private val onEmojiClick: (Emoji) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(val binding: GameLocationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(location: Location, playerEmojisAtLocation: MutableList<Emoji>, oppEmojisAtLocation: MutableList<Emoji>) {
            binding.locationIconTx.text = location.icon
            binding.locationNameTx.text = location.name
            binding.locationDescTx.text = location.description
            binding.playerScoreTx.text = playerTotals[adapterPosition].toString()
            binding.oppScoreTx.text = oppTotals[adapterPosition].toString()

            // Set up the player emojis RecyclerView
            val playerEmojiAdapter = GameEmojiAdapter(playerEmojisAtLocation) { emoji ->
                onEmojiClick(emoji)
            }
            binding.playerEmojiRv.adapter = playerEmojiAdapter

            // Set up the opponent emojis RecyclerView
            val oppEmojiAdapter = GameEmojiAdapter(oppEmojisAtLocation) { emoji ->
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
        // Pass the appropriate emoji list for the player and opponent
        val location = locations[position]
        val playerEmojisAtLocation = playerEmojis.getOrElse(position) { mutableListOf() }
        val oppEmojisAtLocation = oppEmojis.getOrElse(position) { mutableListOf() }

        holder.bind(location, playerEmojisAtLocation, oppEmojisAtLocation)
    }

    override fun getItemCount(): Int = locations.size

    // Method to update emojis in a specific location for both player and opponent
    fun updateLocationEmojis(locationIndex: Int, newPlayerEmojisAtLocation: MutableList<Emoji>, newOppEmojisAtLocation: MutableList<Emoji>) {
        if (locationIndex in playerEmojis.indices && locationIndex in oppEmojis.indices) {
            // Update the emoji lists for player and opponent at the given location
            playerEmojis[locationIndex] = newPlayerEmojisAtLocation
            oppEmojis[locationIndex] = newOppEmojisAtLocation

            // Notify the adapter that this specific location has changed
            notifyItemChanged(locationIndex)
        }
    }

    fun setLocationsEmojis(newPlayerEmojis: MutableList<MutableList<Emoji>>, newOppEmojis: MutableList<MutableList<Emoji>>) {
        playerEmojis = newPlayerEmojis
        oppEmojis = newOppEmojis
        notifyDataSetChanged()  // Notify the adapter that the data has changed
    }

    fun addToLocation(emoji: Emoji, locationIndex: Int, isPlayerMove: Boolean) {
        // Ensure the location index is valid
        if (locationIndex !in locations.indices) return

        // Determine which list to update (player's or opponent's emojis)
        val targetList = if (isPlayerMove) playerEmojis else oppEmojis

        // Get the emoji list for the specific location
        val emojiList = targetList[locationIndex]

        // Add the emoji to the list
        emojiList.add(emoji)

        notifyItemChanged(locationIndex)
    }

    fun updateAllTotals(newPlayerTotals: List<Int>, newOppTotals: List<Int>) {
        playerTotals = newPlayerTotals
        oppTotals = newOppTotals

        // Notify the adapter that the entire dataset has changed
        notifyDataSetChanged()
    }

}