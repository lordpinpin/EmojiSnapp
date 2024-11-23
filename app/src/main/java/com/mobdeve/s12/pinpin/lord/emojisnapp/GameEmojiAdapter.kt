package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.GameEmojiBinding

class GameEmojiAdapter (var emojiList: MutableList<Emoji>, val onEmojiClick: (Emoji) -> Unit) : RecyclerView.Adapter<GameEmojiAdapter.EmojiViewHolder>()
    {

    private var currentRecyclerView : RecyclerView? = null

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

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        currentRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        currentRecyclerView = null
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

    fun triggerEmojiDestruction(position: Int) {
        Log.d("GameEmojiAdapter","Emoji destroy trigger.")
        if (position in emojiList.indices) {
            val emoji = emojiList[position]
            emoji.isDestroyed = true  // Mark the emoji as destroyed in your data model

            val emojiView = currentRecyclerView?.findViewHolderForAdapterPosition(position)?.itemView
            if (emojiView != null) {
                animateEmojiDestruction(emojiView) {
                    // Remove the emoji after the destruction animation
                    emojiList.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    private fun animateEmojiDestruction(emojiView: View, onEnd: () -> Unit) {
        // Individual animations
        val shrinkX = ObjectAnimator.ofFloat(emojiView, "scaleX", 1f, 0f)
        val shrinkY = ObjectAnimator.ofFloat(emojiView, "scaleY", 1f, 0f)
        val rotate = ObjectAnimator.ofFloat(emojiView, "rotation", 0f, 360f)
        val fadeOut = ObjectAnimator.ofFloat(emojiView, "alpha", 1f, 0f)

        // Combine the animations into an AnimatorSet
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(shrinkX, shrinkY, rotate, fadeOut)
        animatorSet.duration = 500 // Set the duration for the destruction animation

        // Add the listener to the AnimatorSet
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onEnd()  // Trigger your end of animation callback
            }
        })

        // Start the animations
        animatorSet.start()
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