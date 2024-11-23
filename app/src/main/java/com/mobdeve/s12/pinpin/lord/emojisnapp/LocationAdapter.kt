package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.GameLocationBinding


class LocationAdapter(
    private var locations: List<Location>,
    private var playerEmojis: MutableList<MutableList<Emoji>>,
    private var oppEmojis: MutableList<MutableList<Emoji>>,
    private var playerTotals: List<Int>,
    private var oppTotals: List<Int>,
    private val onEmojiClick: (Emoji) -> Unit,
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    // List to hold the emoji adapters for player and opponent
    private var playerEmojiAdapters = mutableListOf<GameEmojiAdapter>()
    private var oppEmojiAdapters = mutableListOf<GameEmojiAdapter>()

    inner class LocationViewHolder(val binding: GameLocationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(location: Location, playerEmojisAtLocation: MutableList<Emoji>, oppEmojisAtLocation: MutableList<Emoji>, playerAdapter: GameEmojiAdapter, oppAdapter: GameEmojiAdapter) {
            binding.locationIconTx.text = location.icon
            binding.locationNameTx.text = location.name
            binding.locationDescTx.text = location.description
            binding.playerScoreTx.text = playerTotals[adapterPosition].toString()
            binding.oppScoreTx.text = oppTotals[adapterPosition].toString()

            // Set the emoji adapters for the player and opponent
            binding.playerEmojiRv.adapter = playerAdapter
            binding.oppEmojiRv.adapter = oppAdapter
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
        // Get the appropriate emoji lists for the player and opponent
        val location = locations[position]
        val playerEmojisAtLocation = playerEmojis.getOrElse(position) { mutableListOf() }
        val oppEmojisAtLocation = oppEmojis.getOrElse(position) { mutableListOf() }

        // Ensure the adapter lists are populated for each location
        if (playerEmojiAdapters.size <= position) {
            playerEmojiAdapters.add(GameEmojiAdapter(playerEmojisAtLocation) { emoji -> onEmojiClick(emoji) })
        }
        if (oppEmojiAdapters.size <= position) {
            oppEmojiAdapters.add(GameEmojiAdapter(oppEmojisAtLocation) { emoji -> onEmojiClick(emoji) })
        }

        // Bind the data to the view holder
        holder.bind(location, playerEmojisAtLocation, oppEmojisAtLocation, playerEmojiAdapters[position], oppEmojiAdapters[position])
    }

    override fun getItemCount(): Int = locations.size

    // Method to update emojis in a specific location for both player and opponent
    fun updateLocationEmojis(locationIndex: Int, newPlayerEmojisAtLocation: MutableList<Emoji>, newOppEmojisAtLocation: MutableList<Emoji>) {
        if (locationIndex in playerEmojis.indices && locationIndex in oppEmojis.indices) {
            // Update the emoji lists for player and opponent at the given location
            playerEmojis[locationIndex] = newPlayerEmojisAtLocation
            oppEmojis[locationIndex] = newOppEmojisAtLocation

            // Update the emoji adapters for the location
            playerEmojiAdapters[locationIndex] = GameEmojiAdapter(newPlayerEmojisAtLocation) { emoji -> onEmojiClick(emoji) }
            oppEmojiAdapters[locationIndex] = GameEmojiAdapter(newOppEmojisAtLocation) { emoji -> onEmojiClick(emoji) }

            // Notify the adapter that this specific location has changed
            notifyItemChanged(locationIndex)
        }
    }

    fun updateEmojis() {
        for(i in 0 until 5){
            // Update the emoji adapters for the location
            playerEmojiAdapters[i].updateEmoji()
            oppEmojiAdapters[i].updateEmoji()
        }
    }

    fun setLocationsEmojis(newPlayerEmojis: MutableList<MutableList<Emoji>>, newOppEmojis: MutableList<MutableList<Emoji>>) {
        // Validate that the new emoji lists have the same size as the locations
        if (newPlayerEmojis.size != locations.size || newOppEmojis.size != locations.size) {
            Log.e("LocationAdapter", "Error: Emoji lists must match the number of locations.")
            return
        }

        // Iterate through each location and update its emojis
        for (i in locations.indices) {
            updateLocationEmojis(i, newPlayerEmojis[i], newOppEmojis[i])
        }
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

        // Update the emoji adapter
        if (isPlayerMove) {
            playerEmojiAdapters[locationIndex] = GameEmojiAdapter(emojiList) { emoji -> onEmojiClick(emoji) }
        } else {
            oppEmojiAdapters[locationIndex] = GameEmojiAdapter(emojiList) { emoji -> onEmojiClick(emoji) }
        }

        notifyItemChanged(locationIndex)
    }

    fun updateAllTotals(newPlayerTotals: List<Int>, newOppTotals: List<Int>) {
        playerTotals = newPlayerTotals
        oppTotals = newOppTotals

        // Notify the adapter that the entire dataset has changed
        notifyDataSetChanged()
    }

    fun destroyEmojis(allDestroyed: List<Triple<Int, Int, Boolean>>, onComplete: () -> Unit) {
        val sortedDestroyed = allDestroyed.sortedWith(compareByDescending<Triple<Int, Int, Boolean>> { it.first }
            .thenByDescending { it.second })

        if(sortedDestroyed.isNotEmpty()) {
            // Create a handler to schedule the destruction
            val handler = android.os.Handler(android.os.Looper.getMainLooper())

            // Determine the total time required for all animations
            val totalDelay =
                (sortedDestroyed.size - 1) * 500L // Delay for the last emoji destruction

            sortedDestroyed.forEachIndexed { index, emoji ->
                handler.postDelayed({
                    Log.d("LocationAdapter", "Emoji destroyed: $emoji")

                    // Check if the emoji belongs to the player or opponent
                    if (emoji.third) { // Player Emoji
                        val adapter = playerEmojiAdapters[emoji.first]
                        adapter.triggerEmojiDestruction(emoji.second)
                    } else { // Opponent Emoji
                        val adapter = oppEmojiAdapters[emoji.first]
                        adapter.triggerEmojiDestruction(emoji.second)
                    }
                }, index * 500L) // Delay increases with each index
            }

            // Schedule onComplete to execute after all animations are finished
            handler.postDelayed(
                {
                    onComplete()
                },
                totalDelay + 500L
            ) // Add an extra delay to ensure all destructions are visibly completed
        } else {
            onComplete()
        }
    }
}