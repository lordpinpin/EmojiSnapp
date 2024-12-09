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
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.mobdeve.s12.pinpin.lord.emojisnapp.EmojiType
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityDeckBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityMenuBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.ActivityTrackBinding
import com.mobdeve.s12.pinpin.lord.emojisnapp.databinding.DialogEmojiDetailsBinding

class DeckActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityDeckBinding
    // TODO: Load currently chosen deck in currentDecksChosen in Firebase
    private lateinit var currentDeckChosen: Deck
    private lateinit var sensorManager: SensorManager
    private lateinit var rotationVectorSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDeckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)!!

        // Deck Title
        binding.deckTitleTx.visibility = View.VISIBLE
        binding.deckTitleEt.visibility = View.GONE

        binding.deckTitleTx.setOnClickListener {
            binding.deckTitleTx.visibility = View.GONE
            binding.deckTitleEt.visibility = View.VISIBLE
            binding.deckTitleEt.setText(binding.deckTitleEt.text)
            binding.deckTitleEt.requestFocus()
        }

        // Makes B&W
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorFilter = ColorMatrixColorFilter(colorMatrix)
        binding.deckBackBtn.paint.colorFilter = colorFilter


        binding.deckTitleEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.deckTitleTx.text = binding.deckTitleEt.text
                binding.deckTitleTx.visibility = View.VISIBLE
                binding.deckTitleEt.visibility = View.GONE
            }
        }

        // TODO: Fetch decks that player owns
        var deckData = DeckGenerator.loadDecks()
        deckData.forEach { it.sort() }
        currentDeckChosen = deckData[0]

        var deckEmojiAdapter = DeckEmojiAdapter(deckData[0].getEmojis()) { emoji ->
            showEmojiDetailsPopup(emoji, EmojiType.DECK_EMOJI)
        }
        binding.deckEmojiRv.layoutManager = GridLayoutManager(this, 6)
        binding.deckEmojiRv.adapter = deckEmojiAdapter
        binding.deckEmojiRv.isNestedScrollingEnabled = false
        binding.deckEmojiRv.setHasFixedSize(true)
        binding.deckTitleTx.text = deckData[0].getTitle()
        deckEmojiAdapter.notifyItemRangeChanged(0, deckData[0].getEmojis().size)


        val deckBackAdapter = DeckBackAdapter(deckData) { selectedDeck ->
            updateEmojiRecycler(selectedDeck)
        }
        binding.deckRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.deckRv.adapter = deckBackAdapter

        binding.emojiListRv.layoutManager = GridLayoutManager(this, 4)
        val allEmojis = EmojiFactory.getEmojisSortedByName()
        val sortedEmojis = allEmojis.sortedWith(compareBy({ it.baseCost }, { it.basePower }))
        binding.emojiListRv.adapter = EmojiListAdapter(sortedEmojis) { emoji ->
            showEmojiDetailsPopup(emoji, EmojiType.LIST_EMOJI)
        }


        binding.createBtn.setOnClickListener {
            updateEmojiRecycler(Deck("Empty Deck", arrayListOf<Emoji>()))
        }

        val progress = intent.getIntExtra("CUR_LEVEL", 28);

        binding.deckBackBtn.setOnClickListener {
            val decksReference = FirebaseDatabase.getInstance().getReference("currentDecksChosen")
            val deckJson = Gson().toJson(currentDeckChosen)
            val currentUserUid = Firebase.auth.currentUser?.uid

            if (currentUserUid != null) {
                decksReference.child(currentUserUid).setValue(deckJson)
                    .addOnSuccessListener { Log.d("DeckSave", "Deck saved successfully!") }
                    .addOnFailureListener { Log.e("DeckSave", "Failed to save deck", it) }
            }

            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the rotation vector sensor listener
        rotationVectorSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister the sensor listener to save resources
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            // Convert the rotation vector data to a rotation matrix
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            // Extract orientation angles (azimuth, pitch, roll) from the rotation matrix
            val orientationAngles = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            // Get pitch and roll angles (in degrees)
            var pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat() // Front-back tilt
            var roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()  // Side-to-side tilt

            // Clamp the values to a specific range to prevent excessive tilting
            pitch = clampTilt(pitch, -30f, 30f)  // Example limits for pitch
            roll = clampTilt(roll, -30f, 30f)    // Example limits for roll

            // Update both RecyclerViews with the clamped tilt values
            binding.deckEmojiRv.adapter?.let { adapter ->
                if (adapter is DeckEmojiAdapter) {
                    adapter.updateTiltAngles(pitch, roll)
                }
            }

            binding.emojiListRv.adapter?.let { adapter ->
                if (adapter is EmojiListAdapter) {
                    adapter.updateTiltAngles(pitch, roll)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No implementation needed for this case
    }

    private fun updateEmojiRecycler(deck: Deck) {
        var deckEmojiAdapter = DeckEmojiAdapter(deck.getEmojis()) { emoji ->
            showEmojiDetailsPopup(emoji, EmojiType.DECK_EMOJI)
        }
        binding.deckEmojiRv.adapter = deckEmojiAdapter
        binding.deckTitleTx.text = deck.getTitle()
        deckEmojiAdapter.notifyItemRangeChanged(0, deck.getEmojis().size)
    }

    private fun clampTilt(value: Float, min: Float, max: Float): Float {
        return Math.max(min, Math.min(max, value))
    }

    private fun showEmojiDetailsPopup(emoji: Emoji, type: EmojiType) {

        val binding = DialogEmojiDetailsBinding.inflate(layoutInflater)
        binding.detailEmojiIconTx.text = emoji.icon
        binding.detailEmojiCostTx.text = "${emoji.baseCost}"
        binding.detailEmojiPowerTx.text = "${emoji.basePower}"
        binding.detailEmojiNameTx.text = emoji.name
        binding.detailEmojiDescTx.setText(emoji.description)

        if(type === EmojiType.DECK_EMOJI) {
            binding.addBtn.visibility = View.GONE
        } else {
            binding.removeBtn.visibility = View.GONE
        }

        val dialog = AlertDialog.Builder(this, R.style.TransparentAlertDialog)
            .setView(binding.root)
            .create()

        dialog.show()
    }
}