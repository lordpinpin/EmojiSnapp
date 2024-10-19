package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.LeftTrackBinding

import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.RightTrackBinding

class TrackAdapter(
    private val emojiList: List<Emoji>,
    private val overall_progress: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_LEFT = 0
    private val ITEM_TYPE_RIGHT = 1



    override fun getItemViewType(position: Int): Int {
        // Alternate between left and right items
        return if (position % 2 == 0) ITEM_TYPE_LEFT else ITEM_TYPE_RIGHT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_LEFT) {
            val binding = LeftTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EmojiViewHolderLeft(binding)
        } else {
            val binding = RightTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EmojiViewHolderRight(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val emoji = emojiList[position]
        if (holder is EmojiViewHolderLeft) {
            var cur_progress = overall_progress - 5 * (position)
            cur_progress = minOf(cur_progress, 5)
            holder.bind(emoji, cur_progress)
        } else if (holder is EmojiViewHolderRight) {
            var cur_progress = overall_progress - 5 * (position)
            cur_progress = minOf(cur_progress, 5)
            holder.bind(emoji, cur_progress)
        }
    }

    override fun getItemCount(): Int {
        return emojiList.size
    }


    // ViewHolder for Left-Aligned Emoji
    class EmojiViewHolderLeft(private val binding: LeftTrackBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind(emoji: Emoji, progress: Int) {
            binding.leftEmojiTx.text = emoji.icon
            binding.leftNameTx.text = emoji.name
            binding.leftCostTx.text = emoji.cost.toString()
            binding.leftPowerTx.text = emoji.power.toString()
            binding.leftPb.progress = progress;

            val colorMatrix = ColorMatrix()

            if (progress < 5) {
                colorMatrix.setSaturation(0f) // Set saturation to 0 to remove color
            } else {
                colorMatrix.setSaturation(1f) // Reset saturation to full color
            }

            // Create a ColorFilter using the ColorMatrix
            val colorFilter = ColorMatrixColorFilter(colorMatrix)

            // Apply the ColorFilter to the TextView elements
            binding.leftEmojiTx.paint.colorFilter = colorFilter
            binding.leftCostTx.paint.colorFilter = colorFilter
            binding.leftPowerTx.paint.colorFilter = colorFilter

            // Ensure the TextView is invalidated to refresh the appearance
            binding.leftEmojiTx.invalidate()
            binding.leftCostTx.invalidate()
            binding.leftPowerTx.invalidate()
        }
    }

    // ViewHolder for Right-Aligned Emoji
    class EmojiViewHolderRight(private val binding: RightTrackBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(emoji: Emoji, progress: Int) {
            binding.rightEmojiTx.text = emoji.icon
            binding.rightNameTx.text = emoji.name
            binding.rightCostTx.text = emoji.cost.toString()
            binding.rightPowerTx.text = emoji.power.toString()
            binding.rightPb.progress = progress;

            val colorMatrix = ColorMatrix()

            if (progress < 5) {
                colorMatrix.setSaturation(0f) // Set saturation to 0 to remove color
            } else {
                colorMatrix.setSaturation(1f) // Reset saturation to full color
            }

            // Create a ColorFilter using the ColorMatrix
            val colorFilter = ColorMatrixColorFilter(colorMatrix)

            // Apply the ColorFilter to the TextView elements
            binding.rightEmojiTx.paint.colorFilter = colorFilter
            binding.rightCostTx.paint.colorFilter = colorFilter
            binding.rightPowerTx.paint.colorFilter = colorFilter

            // Ensure the TextView is invalidated to refresh the appearance
            binding.rightEmojiTx.invalidate()
            binding.rightCostTx.invalidate()
            binding.rightPowerTx.invalidate()
        }
    }
}