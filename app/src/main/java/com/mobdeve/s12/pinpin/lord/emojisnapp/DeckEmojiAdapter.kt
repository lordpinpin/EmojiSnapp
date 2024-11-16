package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.DeckEmojiBinding

class DeckEmojiAdapter (private val emojiList: List<Emoji>, private val onEmojiClick: (Emoji) -> Unit) : RecyclerView.Adapter<DeckEmojiAdapter.EmojiViewHolder>() {

    inner class EmojiViewHolder(val binding: DeckEmojiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(emoji: Emoji) {
            binding.deckEmojiIconTx.text = emoji.icon
            binding.deckEmojiCostTx.text = emoji.baseCost.toString()
            binding.deckEmojiPowerTx.text = emoji.basePower.toString()
            binding.deckEmojiNameTx.text = emoji.name

            binding.root.setOnClickListener {
                onEmojiClick(emoji)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val binding = DeckEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmojiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojiList[position])
    }

    override fun getItemCount(): Int = emojiList.size
}