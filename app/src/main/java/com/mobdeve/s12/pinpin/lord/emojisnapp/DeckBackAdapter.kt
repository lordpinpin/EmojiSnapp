package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.DeckBacksBinding

class DeckBackAdapter(
    private val deckList: List<Deck>,
    private val onDeckClick: (Deck) -> Unit) : RecyclerView.Adapter<DeckBackAdapter.DeckViewHolder>() {

    // ViewHolder to hold and manage the views for each item
    inner class DeckViewHolder(val binding: DeckBacksBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(deck: Deck) {
            // Update the icon name TextView with the iconName from the Deck object
            binding.deckBackTitleTx.text = deck.getTitle()
            binding.root.setOnClickListener {
                onDeckClick(deck)
            }
        }
    }

    // Called to create a new ViewHolder when there is no existing one to reuse
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val binding = DeckBacksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeckViewHolder(binding)
    }

    // Called to bind data to the ViewHolder at a specific position
    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        holder.bind(deckList[position])
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = deckList.size
}