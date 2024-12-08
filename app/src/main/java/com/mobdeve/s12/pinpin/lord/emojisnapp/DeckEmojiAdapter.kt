package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.DeckEmojiBinding

class DeckEmojiAdapter(
    private val emojiList: List<Emoji>,
    private val onEmojiClick: (Emoji) -> Unit
) : RecyclerView.Adapter<DeckEmojiAdapter.EmojiViewHolder>() {

    private var currentPitch: Float = 0f
    private var currentRoll: Float = 0f

    // Function to update tilt angles from the sensor
    fun updateTiltAngles(pitch: Float, roll: Float) {
        currentPitch = pitch
        currentRoll = roll

        notifyItemRangeChanged(0, itemCount)
    }

    inner class EmojiViewHolder(val binding: DeckEmojiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(emoji: Emoji, pitch: Float, roll: Float) {
            // Bind emoji data
            binding.deckEmojiIconTx.text = emoji.icon
            binding.deckEmojiCostTx.text = emoji.baseCost.toString()
            binding.deckEmojiPowerTx.text = emoji.basePower.toString()
            binding.deckEmojiNameTx.text = emoji.name

            // Apply tilt to the emoji icon TextView
            binding.deckEmojiIconTx.rotationX = pitch / 2  // Forward-backward tilt
            binding.deckEmojiIconTx.rotationY = roll / 2   // Side-to-side tilt

            // Click listener for the emoji
            binding.root.setOnClickListener {
                onEmojiClick(emoji)
            }
        }

        fun updateTilt() {
            binding.deckEmojiIconTx.rotationX = currentPitch / 2
            binding.deckEmojiIconTx.rotationY = currentRoll / 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val binding = DeckEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmojiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        // Pass the current tilt values to the bind function
        holder.bind(emojiList[position], currentPitch, currentRoll)
    }

    override fun onViewAttachedToWindow(holder: EmojiViewHolder) {
        super.onViewAttachedToWindow(holder)
        // Update tilt when the view is attached
        holder.updateTilt()
    }

    override fun getItemCount(): Int = emojiList.size
}