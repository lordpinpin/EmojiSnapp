package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.GameEmojiBinding

class GameEmojiAdapter (var emojiList: MutableList<Emoji>, val onEmojiClick: (Emoji) -> Unit) : RecyclerView.Adapter<GameEmojiAdapter.EmojiViewHolder>()
    {

    inner class EmojiViewHolder(val binding: GameEmojiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(emoji: Emoji) {
            binding.gameEmojiIconTx.text = emoji.icon
            binding.gameEmojiCostTx.text = emoji.baseCost.toString()
            binding.gameEmojiPowerTx.text = emoji.currentPower.toString()

            animateEmojiAddition(binding.root)

            binding.root.setOnClickListener {
                onEmojiClick(emoji)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val binding = GameEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmojiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojiList[position])
    }

    override fun getItemCount(): Int = emojiList.size

    private fun animateEmojiAddition(emojiView: View) {
        val fadeIn = ObjectAnimator.ofFloat(emojiView, "alpha", 0f, 1f)
        fadeIn.duration = 500  // Set the duration for the fade-in animation
        fadeIn.start()
    }

    fun setEmojis(newEmojis: MutableList<Emoji>) {
        Log.d("GameEmojiAdapter", "$newEmojis")
        emojiList = newEmojis

        notifyDataSetChanged()  // Notify the adapter that the data has changed
    }

    fun updateEmoji(){
        for(i in 0..emojiList.size){
            notifyItemChanged(i)
        }
    }

}