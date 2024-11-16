package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class NoScrollLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun canScrollVertically(): Boolean {
        return false  // Disable vertical scrolling
    }

    override fun canScrollHorizontally(): Boolean {
        return false  // Disable horizontal scrolling
    }
}