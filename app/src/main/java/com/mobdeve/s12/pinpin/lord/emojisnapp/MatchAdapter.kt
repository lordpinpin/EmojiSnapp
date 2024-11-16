package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.DeckEmojiBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.MatchDetailsBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MatchAdapter(
    private val matches: List<Match>,
    private val onEmojiClick: (Emoji) -> Unit // Pass click listener from outside
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    inner class MatchViewHolder(private val binding: MatchDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(match: Match) {
            // Set the match details to the TextViews using View Binding
            binding.matchOppTx.text = "vs. " + match.opponent
            binding.matchResultTx.text = match.result.resultString
            binding.matchValueTx.text = "${match.value}"

            // Format the date to a readable string
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.matchDateTx.text = formatter.format(match.date)

            // Set up player's deck RecyclerView
            val myDeckAdapter = DeckEmojiAdapter(match.deck.getEmojis()) { emoji ->
                onEmojiClick(emoji)
            }
            binding.myDeckRv.layoutManager = GridLayoutManager(binding.root.context, 6)
            binding.myDeckRv.adapter = myDeckAdapter
            binding.myDeckRv.isNestedScrollingEnabled = false
            binding.myDeckRv.setHasFixedSize(true)

            // Set up opponent's deck RecyclerView
            val oppDeckAdapter = DeckEmojiAdapter(match.oppDeck.getEmojis()) { emoji ->
                onEmojiClick(emoji)
            }
            binding.oppDeckRv.layoutManager = GridLayoutManager(binding.root.context, 6)
            binding.oppDeckRv.adapter = oppDeckAdapter
            binding.oppDeckRv.isNestedScrollingEnabled = false
            binding.oppDeckRv.setHasFixedSize(true)

            binding.dropdown.visibility = View.GONE
            binding.matchDownBtn.setOnClickListener {
                if (binding.dropdown.visibility == View.GONE) {
                    expandView(binding.dropdown)
                    ObjectAnimator.ofFloat(binding.matchDownBtn, "rotation", 180f).apply {
                        duration = 300 // Duration of the animation in milliseconds
                        start()
                    }
                } else {
                    collapseView(binding.dropdown)
                    ObjectAnimator.ofFloat(binding.matchDownBtn, "rotation", 0f).apply {
                        duration = 300 // Duration of the animation in milliseconds
                        start()
                    }
                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        // Use View Binding to inflate the item layout
        val binding = MatchDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(matches[position])
    }

    override fun getItemCount(): Int = matches.size

    // TODO: Find a simpler version that's not choppy
    private fun expandView(view: View) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val targetHeight = view.measuredHeight

        view.layoutParams.height = 1
        view.visibility = View.VISIBLE

        // Enable hardware layer
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        val startTime = System.currentTimeMillis()
        val duration = 500L

        val frameCallback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                val elapsedTime = System.currentTimeMillis() - startTime
                val progress = (elapsedTime.toFloat() / duration).coerceIn(0f, 1f)

                // Apply easing with interpolation
                val interpolatedProgress = android.view.animation.AccelerateDecelerateInterpolator().getInterpolation(progress)
                view.layoutParams.height = (1 + interpolatedProgress * (targetHeight - 1)).toInt()
                view.requestLayout()

                if (progress < 1f) {
                    Choreographer.getInstance().postFrameCallback(this)
                } else {
                    view.setLayerType(View.LAYER_TYPE_NONE, null)
                }
            }
        }
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }


    private fun collapseView(view: View) {
        val initialHeight = view.measuredHeight

        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.addUpdateListener { animation ->
            view.layoutParams.height = animation.animatedValue as Int
            view.requestLayout()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
            }
        })
        animator.duration = 300
        animator.start()
    }
}

