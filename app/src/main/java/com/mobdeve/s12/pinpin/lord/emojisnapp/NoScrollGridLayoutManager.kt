package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class NoScrollGridLayoutManager(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount) {
    override fun canScrollVertically(): Boolean {
        return false  // Disable vertical scrolling
    }

    override fun canScrollHorizontally(): Boolean {
        return false  // Disable horizontal scrolling
    }
}