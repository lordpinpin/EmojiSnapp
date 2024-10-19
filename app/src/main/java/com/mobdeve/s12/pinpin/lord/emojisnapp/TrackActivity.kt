package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityTrackBinding

class TrackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emojiList = DataGenerator.loadData()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        val progress = intent.getIntExtra("CUR_LEVEL", 17);

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = TrackAdapter(emojiList, progress, this)
        binding.recyclerView.scrollToPosition(0)

        // Makes B&W
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorFilter = ColorMatrixColorFilter(colorMatrix)
        binding.backBtn.paint.colorFilter = colorFilter

        binding.backBtn.setOnClickListener {
            finish() // Ends the current activity
        }
    }
}