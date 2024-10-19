package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OverlapItemDecoration (
    private val overlapValue: Int,
    private val targetPosition: Int // Only overlap at this specific position
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val itemPosition = parent.getChildAdapterPosition(view)

            // Apply overlap only for the specific position
            if (itemPosition == targetPosition) {
                outRect.top = -overlapValue // Apply a negative offset to create overlap
            }
        }
}