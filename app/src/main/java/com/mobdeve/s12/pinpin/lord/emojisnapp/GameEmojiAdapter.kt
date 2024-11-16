package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.GameEmojiBinding

class GameEmojiAdapter (val emojiList: List<Emoji>, val onEmojiClick: (Emoji) -> Unit) : RecyclerView.Adapter<GameEmojiAdapter.EmojiViewHolder>()
    {

    inner class EmojiViewHolder(val binding: GameEmojiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(emoji: Emoji) {
            binding.gameEmojiIconTx.text = emoji.icon
            binding.gameEmojiCostTx.text = emoji.baseCost.toString()
            binding.gameEmojiPowerTx.text = emoji.basePower.toString()

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
}