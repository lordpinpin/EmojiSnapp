package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.DialogEmojiDetailsBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.LeftTrackBinding

import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.RightTrackBinding

class TrackAdapter(
    private val emojiList: List<Emoji>,
    private val overall_progress: Int,
    private val context: Context
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
            EmojiViewHolderLeft(binding, context)
        } else {
            val binding = RightTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EmojiViewHolderRight(binding, context)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val emoji = emojiList[position]
        if (holder is EmojiViewHolderLeft) {
            var req = position * 5
            var cur_progress = overall_progress - req
            cur_progress = minOf(cur_progress, 5)
            holder.bind(emoji, cur_progress, req)
        } else if (holder is EmojiViewHolderRight) {
            var req = position * 5
            var cur_progress = overall_progress - req
            cur_progress = minOf(cur_progress, 5)
            holder.bind(emoji, cur_progress, req)
        }
    }

    override fun getItemCount(): Int {
        return emojiList.size
    }


    // ViewHolder for Left-Aligned Emoji
    class EmojiViewHolderLeft(private val binding: LeftTrackBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root)  {
        fun bind(emoji: Emoji, progress: Int, req: Int) {
            binding.leftEmojiTx.text = emoji.icon
            binding.leftNameTx.text = emoji.name
            binding.leftCostTx.text = emoji.baseCost.toString()
            binding.leftPowerTx.text = emoji.basePower.toString()
            binding.leftPb.progress = progress;
            binding.leftReqTx.text = req.toString()

            val colorMatrix = ColorMatrix()

            if (progress < 5) {
                colorMatrix.setSaturation(0f) // Set saturation to 0 to remove color
            } else {
                colorMatrix.setSaturation(1f) // Reset saturation to full color
            }

            binding.root.setOnClickListener {
                showEmojiDetailsDialog(emoji)
            }

            // Create a ColorFilter using the ColorMatrix
            val colorFilter = ColorMatrixColorFilter(colorMatrix)

            // Apply the ColorFilter to the TextView elements
            binding.leftEmojiTx.paint.colorFilter = colorFilter
            binding.leftCostTx.paint.colorFilter = colorFilter
            binding.leftPowerTx.paint.colorFilter = colorFilter
            binding.leftReqTx.paint.colorFilter = colorFilter

        }

        private fun showEmojiDetailsDialog(emoji: Emoji) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_emoji_details, null)
            val binding = DialogEmojiDetailsBinding.bind(dialogView)

            // Set emoji details
            binding.detailEmojiIconTx.text = emoji.icon
            binding.detailEmojiCostTx.text = "${emoji.baseCost}"
            binding.detailEmojiPowerTx.text = "${emoji.basePower}"
            binding.detailEmojiNameTx.text = emoji.name
            binding.detailEmojiDescTx.setText(emoji.description)

            binding.addBtn.visibility = View.GONE
            binding.modifyBtn.visibility = View.GONE
            binding.removeBtn.visibility = View.GONE

            val dialog = AlertDialog.Builder(context, R.style.TransparentAlertDialog)
                .setView(binding.root)
                .create()

            dialog.show()
        }
    }

    // ViewHolder for Right-Aligned Emoji
    class EmojiViewHolderRight(private val binding: RightTrackBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(emoji: Emoji, progress: Int, req: Int) {
            binding.rightEmojiTx.text = emoji.icon
            binding.rightNameTx.text = emoji.name
            binding.rightCostTx.text = emoji.baseCost.toString()
            binding.rightPowerTx.text = emoji.basePower.toString()
            binding.rightPb.progress = progress;
            binding.rightReqTx.text = req.toString()

            val colorMatrix = ColorMatrix()

            if (progress < 5) {
                colorMatrix.setSaturation(0f) // Set saturation to 0 to remove color
            } else {
                colorMatrix.setSaturation(1f) // Reset saturation to full color
            }

            binding.root.setOnClickListener {
                showEmojiDetailsDialog(emoji)
            }

            // Create a ColorFilter using the ColorMatrix
            val colorFilter = ColorMatrixColorFilter(colorMatrix)

            // Apply the ColorFilter to the TextView elements
            binding.rightEmojiTx.paint.colorFilter = colorFilter
            binding.rightCostTx.paint.colorFilter = colorFilter
            binding.rightPowerTx.paint.colorFilter = colorFilter
            binding.rightReqTx.paint.colorFilter = colorFilter

        }

        private fun showEmojiDetailsDialog(emoji: Emoji) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_emoji_details, null)
            val binding = DialogEmojiDetailsBinding.bind(dialogView)

            // Set emoji details
            binding.detailEmojiIconTx.text = emoji.icon
            binding.detailEmojiCostTx.text = "${emoji.baseCost}"
            binding.detailEmojiPowerTx.text = "${emoji.basePower}"
            binding.detailEmojiNameTx.text = emoji.name
            binding.detailEmojiDescTx.setText(emoji.description)

            binding.addBtn.visibility = View.GONE
            binding.modifyBtn.visibility = View.GONE
            binding.removeBtn.visibility = View.GONE

            val dialog = AlertDialog.Builder(context, R.style.TransparentAlertDialog)
                .setView(binding.root)
                .create()

            dialog.show()
        }
    }


}