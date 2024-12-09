package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.DeckEmojiAdapter.EmojiViewHolder
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ListEmojiBinding

class EmojiListAdapter(private val emojiList: List<Emoji>, private val onEmojiClick: (Emoji) -> Unit) : RecyclerView.Adapter<EmojiListAdapter.EmojiViewHolder>() {

    private var currentPitch: Float = 0f
    private var currentRoll: Float = 0f

    // Function to update tilt angles from the sensor
    fun updateTiltAngles(pitch: Float, roll: Float) {
        currentPitch = pitch
        currentRoll = roll

        notifyItemRangeChanged(0, itemCount)
    }

    inner class EmojiViewHolder(val binding: ListEmojiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(emoji: Emoji, pitch: Float, roll: Float) {
            binding.listEmojiIconTx.text = emoji.icon
            binding.listEmojiCostTx.text = emoji.baseCost.toString()
            binding.listEmojiPowerTx.text = emoji.basePower.toString()
            binding.listEmojiNameTx.text = emoji.name

            binding.listEmojiIconTx.rotationX = pitch / 2
            binding.listEmojiIconTx.rotationY = roll / 2

            binding.root.setOnClickListener {
                onEmojiClick(emoji)
            }
        }

        fun updateTilt() {
            binding.listEmojiIconTx.rotationX = currentPitch / 2
            binding.listEmojiIconTx.rotationY = currentRoll / 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val binding = ListEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmojiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojiList[position], currentPitch, currentRoll)
    }

    override fun onViewAttachedToWindow(holder: EmojiListAdapter.EmojiViewHolder) {
        super.onViewAttachedToWindow(holder)
        // Update tilt when the view is attached
        holder.updateTilt()
    }

    override fun getItemCount(): Int = emojiList.size
}