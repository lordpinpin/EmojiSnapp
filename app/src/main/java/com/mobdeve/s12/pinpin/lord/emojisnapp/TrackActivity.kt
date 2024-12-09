package com.mobdeve.s12.pinpin.lord.emojisnapp

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityTrackBinding

class TrackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrackBinding
    private var trackAdapter: TrackAdapter? = null

    private val instance = FirebaseDatabase.getInstance("https://mco3-7e1e6-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val database = instance.getReference("matches")
    private val user = FirebaseAuth.getInstance().currentUser;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emojiList = EmojiFactory.getEmojisWithPositiveUnlockThreshold()

        Log.d("TrackActivity", "emojiList = $emojiList")

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        var progress = 0
        write(emojiList, layoutManager, progress)


        database.get().addOnSuccessListener {
            val idk = it.value as Map<String, Map<String, Any>>

            var cur_pts = 0L
            idk.forEach {
                val playerName = it.value["player1"] as String
                val points = it.value["player1Points"] as Long

                if(playerName == user?.uid) {
                    cur_pts += points
                    progress = Math.max(progress, cur_pts.toInt())
                }
            }

            Log.e("Progress", "Change to " + progress)
            write(emojiList, layoutManager, progress)
        }

    }

    fun write(emojiList: List<Emoji>, layoutManager: RecyclerView.LayoutManager, progress: Int) {
        trackAdapter = TrackAdapter(emojiList, progress, this)

        binding.collectionRv.layoutManager = layoutManager
        binding.collectionRv.adapter = trackAdapter
        binding.collectionRv.scrollToPosition(0)

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