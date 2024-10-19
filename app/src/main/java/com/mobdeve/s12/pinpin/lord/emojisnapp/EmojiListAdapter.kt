package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ListEmojiBinding

class EmojiListAdapter(private val emojiList: List<Emoji>, private val onEmojiClick: (Emoji) -> Unit) : RecyclerView.Adapter<EmojiListAdapter.EmojiViewHolder>() {

    inner class EmojiViewHolder(val binding: ListEmojiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(emoji: Emoji) {
            binding.listEmojiIconTx.text = emoji.icon
            binding.listEmojiCostTx.text = emoji.cost.toString()
            binding.listEmojiPowerTx.text = emoji.power.toString()
            binding.listEmojiNameTx.text = emoji.name

            binding.root.setOnClickListener {
                onEmojiClick(emoji)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val binding = ListEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmojiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojiList[position])
    }

    override fun getItemCount(): Int = emojiList.size
}